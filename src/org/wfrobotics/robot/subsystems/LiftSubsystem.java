package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.LimitSwitch.Limit;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.lift.LiftZeroThenOpenLoop;
import org.wfrobotics.robot.config.LiftHeight;
import org.wfrobotics.robot.config.RobotConfig;

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
    private static final int kTicksToTop = 27000;
    private static final double kTicksPerInch = kTicksToTop / 38.0;
    private static final double kFeedForwardHasCube = 0.25;  // TODO Keep track even if we tilt wrist
    private static final double kFeedForwardNoCube = 0.20;  // TODO Try increasing to make more buoyant
    private static final double kInchesGroundToZero = LiftHeight.Intake.get();
    private static final int kTickRateBrakeMode = 500;
    private final int kSlotUp, kSlotDown;
    private final boolean kTuning;

    static class SingletonHolder
    {
        static LiftSubsystem instance = new LiftSubsystem();
    }
    private final RobotState state = RobotState.getInstance();
    private final TalonSRX master;
    private final BaseMotorController follower;
    private final LimitSwitch limit;

    private boolean hasZeroed = false;

    private LiftSubsystem()
    {
        RobotConfig config = RobotConfig.getInstance();
        kTuning = config.kLiftTuning;
        kSlotUp = config.LIFT_CLOSED_LOOP.gains.get(0).kSlot;
        kSlotDown = config.LIFT_CLOSED_LOOP.gains.get(1).kSlot;

        master = TalonFactory.makeClosedLoopTalon(config.LIFT_CLOSED_LOOP).get(0);
        master.setSelectedSensorPosition(config.kLiftTicksStartup, 0, 100);
        master.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, 100);
        master.configVelocityMeasurementWindow(1, 100);  // TODO Changed to small value. Is okay?
        master.configNeutralDeadband(0.1, 100);
        master.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 5, 100);  // Faster limit switches
        TalonFactory.configCurrentLimiting(master, 15, 30, 200);  // Observed 10A when holding
        TalonFactory.configFastErrorReporting(master, kTuning);
        //        master.configAllowableClosedloopError(0, 20, 10);
        //        master.configAllowableClosedloopError(1, 20, 10);
        master.configClosedloopRamp(0.15, 100);  // Soften reaching setpoint
        // TODO Try using Status_10_MotionMagic to improve motion?
        // TODO Try configClosedloopRamp() of .1, see if it approaches limits smoother

        follower = TalonFactory.makeFollowers(master, config.LIFT_CLOSED_LOOP.masters.get(0)).get(0);

        limit =  new LimitSwitch(master, config.LIFT_LIMIT_SWITCH_NORMALLY[0], config.LIFT_LIMIT_SWITCH_NORMALLY[1]);
        LimitSwitch.configSoftwareLimitF(master, kTicksToTop, true);
        LimitSwitch.configSoftwareLimitR(master, -500, true);
        LimitSwitch.configHardwareLimitAutoZero(master, false, false);
    }

    public static LiftSubsystem getInstance()
    {
        return SingletonHolder.instance;  // TODO Test if this worked. If so, look into protected member in superclass?
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new LiftZeroThenOpenLoop());
    }

    public void updateSensors()
    {
        zeroIfAtLimit();

        // TODO Cache sensors in private class?

        state.updateLift(getInchesOffGround(), !onTarget());
    }

    public void periodic()
    {
        //        System.out.println("Lift Periodic(), test me");
    }

    public void reportState()
    {
        SmartDashboard.putNumber("Lift Position", master.getSelectedSensorPosition(0));
        SmartDashboard.putNumber("Lift Velocity", master.getSelectedSensorVelocity(0));
        SmartDashboard.putNumber("Lift Height", ticksToInches(getHeightRaw()) + kInchesGroundToZero);
        SmartDashboard.putNumber("Lift Current", master.getOutputCurrent());
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
        final int kTickRateSlowEnough = kTickRateBrakeMode + 200;
        return Math.abs(master.getSelectedSensorVelocity(0)) < kTickRateSlowEnough;
    }

    public void setOpenLoop(double percent)
    {
        final double speed = (AtHardwareLimitTop() && percent > 0.0) ? 0.0 : percent;

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
        final double antigravity= (state.robotHasCube) ? kFeedForwardHasCube : kFeedForwardNoCube;
        final double feedforward = (state.liftHeightInches > kInchesGroundToZero) ? antigravity : 0.0;

        master.set(mode, val, DemandType.ArbitraryFeedForward, feedforward);
    }

    private void debugCalibration()
    {
        Preferences prefs = Preferences.getInstance();
        int slot = prefs.getInt("lift_slot", 0);

        SmartDashboard.putNumber("Lift Error", master.getClosedLoopError(0));

        master.config_kP(slot, prefs.getDouble("lift_p", 0.0), 0);
        master.config_kI(slot, prefs.getDouble("lift_i", 0.0), 0);
        master.config_kD(slot, prefs.getDouble("lift_d", 0.0), 0);
    }

    public boolean runFunctionalTest(boolean includeMotion)
    {
        boolean result = true;

        System.out.println("Lift Test:");
        result &= TalonChecker.checkFirmware(master);
        result &= TalonChecker.checkFirmware(follower);
        result &= TalonChecker.checkEncoder(master);
        result &= TalonChecker.checkFrameRates(master);

        result &= TalonChecker.checkSensorPhase(0.3, master);

        System.out.println(String.format("Lift Test: %s", (result) ? "SUCCESS" : "FAILURE"));
        return result;
    }
}
