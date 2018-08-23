package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.LimitSwitch.Limit;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.SAFMSubsystem;
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

public class LiftSubsystem extends SAFMSubsystem
{
    private static final double kTicksPerRev = 4096.0;
    private static final double kRevsPerInch = 1.0 / 4.555;  // Measured on practice robot
    private static final double kFeedForwardHasCube = 0.0;   // TODO Tune me
    private static final double kFeedForwardNoCube = 0.0;    // TODO Tune me
    private final int kSlotUp;
    private final int kSlotDown;
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
        master.configVelocityMeasurementWindow(32, 10);
        master.configNeutralDeadband(0.1, 10);
        master.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 5, 10);  // Faster limit switches
        TalonFactory.configCurrentLimiting(master, 30, 200, 15);  // Observed 10A when holding

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
        state.updateLiftHeight(getHeightInches());
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

    public double getHeightInches()
    {
        return ticksToInches(getHeightRaw());
    }

    /** Encoder has zeroed at any point and is ready to do some pull ups! */
    public boolean hasZeroed()
    {
        return hasZeroed;
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

    public void setClosedLoop(double heightInches)
    {
        final int slot = (heightInches > getHeightRaw()) ? kSlotUp : kSlotDown;

        master.selectProfileSlot(slot, 0);
        setMotor(ControlMode.MotionMagic, inchesToTicks(heightInches));  // Stalls motors
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

    private static double inchesToTicks(double inches)
    {
        return inches * kRevsPerInch * kTicksPerRev;
    }

    private double ticksToInches(double ticks)
    {
        return ticks / kRevsPerInch / kTicksPerRev;
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

    public boolean runFunctionalTest()
    {
        boolean result = true;

        result &= TalonFactory.checkFirmware(master);
        result &= TalonFactory.checkFirmware(follower);
        result &= TalonFactory.checkEncoder(master);

        System.out.println(String.format("Lift Test: %s", (result) ? "SUCCESS" : "FAILURE"));
        return result;
    }
}
