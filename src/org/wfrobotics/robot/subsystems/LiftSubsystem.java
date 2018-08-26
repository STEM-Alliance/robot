package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.LimitSwitch.Limit;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.lift.LiftZeroThenOpenLoop;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The elevator consists of two independently connected Mini CIM motors to raise/lower the intake and climber
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class LiftSubsystem extends EnhancedSubsystem
{
    private static final double kTicksPerInch = 4096.0 / 4.555;  // TODO Remeasure
    private static final double kFeedForwardHasCube = 0.0;   // TODO Tune me
    private static final double kFeedForwardNoCube = 0.0;    // TODO Tune me
    private static final double kInchesGroundToZero = 0.0;      // TODO Measure me and apply offset to commands too
    private final int kSlotUp, kSlotDown;
    private final boolean kTuning;

    private static LiftSubsystem instance = null;
    private final RobotState state = RobotState.getInstance();
    private final TalonSRX master;
    private final BaseMotorController follower;
    private final LimitSwitch limit;

    private boolean hasZeroed = false;

    private LiftSubsystem()
    {
        RobotConfig config = RobotConfig.getInstance();
        kTuning = config.LIFT_DEBUG;
        kSlotUp = config.LIFT_CLOSED_LOOP.gains.get(0).kSlot;
        kSlotDown = config.LIFT_CLOSED_LOOP.gains.get(1).kSlot;

        master = TalonFactory.makeClosedLoopTalon(config.LIFT_CLOSED_LOOP).get(0);
        master.setSelectedSensorPosition(config.LIFT_TICKS_STARTING, 0, 10);
        master.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, 10);
        master.configVelocityMeasurementWindow(1, 10);  // TODO Changed to small value. Is okay?
        master.configNeutralDeadband(0.1, 10);
        master.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 5, 10);  // Faster limit switches
        TalonFactory.configCurrentLimiting(master, 15, 30, 200);  // Observed 10A when holding
        TalonFactory.configFastErrorReporting(master, kTuning);
        // TODO Try configAllowableClosedloopError()
        // TODO Try using Status_10_MotionMagic to improve motion?
        // TODO Try configClosedloopRamp() of .1, see if it approaches limits smoother

        follower = TalonFactory.makeFollowers(master, config.LIFT_CLOSED_LOOP.masters.get(0)).get(0);

        limit =  new LimitSwitch(master, config.LIFT_LIMIT_SWITCH_NORMALLY[0], config.LIFT_LIMIT_SWITCH_NORMALLY[1]);
        LimitSwitch.configSoftwareLimitF(master, 100000000, true);  // TODO Tune
        LimitSwitch.configSoftwareLimitR(master, -100000000, true);  // TODO Tune
        LimitSwitch.configHardwareLimitAutoZero(master, false, false);
    }

    public static LiftSubsystem getInstance()
    {
        if (instance == null)
        {
            instance = new LiftSubsystem();
        }
        return instance;
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new LiftZeroThenOpenLoop());
    }

    public void updateSensors()
    {
        zeroIfAtLimit();
        state.updateLift(getInchesOffGround(), onTarget());
    }

    public void reportState()
    {
        SmartDashboard.putNumber("Lift Height", ticksToInches(getHeightRaw()));
        SmartDashboard.putBoolean("RB", limit.isSet(Limit.REVERSE));
        SmartDashboard.putBoolean("RT", limit.isSet(Limit.FORWARD));
        if (kTuning)
        {
            debugCalibration();
        }
    }
    public boolean AtHardwareLimitBottom()
    {
        return limit.isSet(Limit.REVERSE);
    }

    public boolean AtHardwareLimitTop()
    {
        return limit.isSet(Limit.FORWARD);
    }

    public double getInchesOffGround()
    {
        return ticksToInches(getHeightRaw()) + kInchesGroundToZero;
    }

    /** Encoder has zeroed at any point and is ready to do some pull ups! */
    public boolean hasZeroed()
    {
        return hasZeroed;
    }

    /** Use to improve isFinished() criteria for closed loop commands? */
    public boolean onTarget()
    {
        final int kTickRateSlowEnough = 100;  // TODO Tune me
        return master.getSelectedSensorVelocity(0) < kTickRateSlowEnough;
    }

    public void setOpenLoop(double percent)
    {
        double speed = percent;

        if (AtHardwareLimitTop() && speed > 0.0)
        {
            speed = 0.0;
        }

        setMotor(ControlMode.PercentOutput, speed);
    }

    public void setClosedLoop(double inchesOffGround)
    {
        final double inchesFromZero = inchesOffGround - kInchesGroundToZero;
        final int slot = (inchesFromZero > getHeightRaw()) ? kSlotUp : kSlotDown;

        master.selectProfileSlot(slot, 0);
        setMotor(ControlMode.MotionMagic, inchesToTicks(inchesFromZero));  // Stalls motors
    }

    public void zeroIfAtLimit()
    {
        if(AtHardwareLimitBottom())
        {
            zeroEncoder();
        }
    }

    public void zeroEncoder()
    {
        master.setSelectedSensorPosition(0, 0, 0);
        hasZeroed = true;
    }

    private double getHeightRaw()
    {
        return master.getSelectedSensorPosition(0);
    }

    private double inchesToTicks(double inches)
    {
        return inches * kTicksPerInch;
    }

    private double ticksToInches(double ticks)
    {
        return ticks / kTicksPerInch;
    }

    private void setMotor(ControlMode mode, double val)
    {
        final double feedforward = (state.robotHasCube) ? kFeedForwardHasCube : kFeedForwardNoCube;

        master.set(mode, val, DemandType.ArbitraryFeedForward, feedforward);
    }

    private void debugCalibration()
    {
        SmartDashboard.putNumber("Lift Position", master.getSelectedSensorPosition(0));
        SmartDashboard.putNumber("Lift Velocity", master.getSelectedSensorVelocity(0));
        SmartDashboard.putNumber("Lift Error", master.getClosedLoopError(0));

        double p = Preferences.getInstance().getDouble("lift_p", 0.0);
        double i = Preferences.getInstance().getDouble("lift_i", 0.0);
        double d = Preferences.getInstance().getDouble("lift_d", 0.0);
        int slot = Preferences.getInstance().getInt("lift_slot", 0);

        master.config_kP(slot, p, 0);
        master.config_kP(slot, p, 0);
        master.config_kI(slot, i, 0);
        master.config_kI(slot, i, 0);
        master.config_kD(slot, d, 0);
        master.config_kD(slot, d, 0);
    }

    public boolean runFunctionalTest(boolean includeMotion)
    {
        boolean result = true;

        result &= TalonChecker.checkFirmware(master);
        result &= TalonChecker.checkFirmware(follower);
        result &= TalonChecker.checkEncoder(master);
        result &= TalonChecker.checkFrameRates(master);

        result &= TalonChecker.checkSensorPhase(0.1, master);

        System.out.println(String.format("Lift Test: %s", (result) ? "SUCCESS" : "FAILURE"));
        return result;
    }
}
