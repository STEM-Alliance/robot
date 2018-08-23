package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.LimitSwitch.Limit;
import org.wfrobotics.reuse.hardware.StallSense;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.SAFMSubsystem;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.wrist.WristZeroThenOpenLoop;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Wrist extends SAFMSubsystem
{
    private static final double kFullRangeDegrees = 90.0;
    private final int kTicksToTop;
    private final boolean kTuning;

    private static Wrist instance = null;
    private final RobotState state = RobotState.getInstance();
    private final TalonSRX motor;
    private final LimitSwitch limits;
    private final StallSense stallSensor;

    private boolean hasZeroed = false;
    private boolean stalled = false;

    private Wrist()
    {
        RobotConfig config = RobotConfig.getInstance();
        kTicksToTop = config.WRIST_TICKS_TO_TOP;
        kTuning = config.WRIST_TUNING;

        motor = TalonFactory.makeClosedLoopTalon(config.WRIST_CLOSED_LOOP).get(0);
        motor.setSelectedSensorPosition(0, 0, 10);  // Before zeroing, report values above smart intake active therehold
        motor.configNeutralDeadband(config.WRIST_DEADBAND, 10);
        motor.configOpenloopRamp(.05, 10);  // TODO Try smaller value in auto
        motor.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, 10);
        motor.configVelocityMeasurementWindow(1, 10);  // TODO Changed to small value. Is okay?
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 5, 10);  // Faster limit switches
        TalonFactory.configCurrentLimiting(motor, 40, 200, 20);  // Adding with high numbers just in case
        if (kTuning)
        {
            TalonFactory.configFastErrorReporting(motor);
        }
        // TODO Try configAllowableClosedloopError()
        // TODO Try using Status_10_MotionMagic to improve motion?

        limits = new LimitSwitch(motor, LimitSwitchNormal.NormallyOpen, LimitSwitchNormal.NormallyOpen);
        LimitSwitch.configSoftwareLimitF(motor, kTicksToTop + 100000000, true);  // TODO Tune
        LimitSwitch.configSoftwareLimitR(motor, -100000000, true);  // TODO Tune
        LimitSwitch.configHardwareLimitAutoZero(motor, false, false);

        stallSensor = new StallSense(motor, 4.0, 0.15);
    }

    public static Wrist getInstance()
    {
        if (instance == null)
        {
            instance = new Wrist();
        }
        return instance;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristZeroThenOpenLoop());
    }

    public void updateSensors()
    {
        stalled = stallSensor.isStalled();
        zeroIfAtLimit();
        state.updateWristPosition(getAngle());
    }

    public void reportState()
    {
        SmartDashboard.putNumber("Wrist Position", motor.getSelectedSensorPosition(0));
        SmartDashboard.putBoolean("Wrist Limit B", AtHardwareLimitBottom());
        SmartDashboard.putBoolean("Wrist Limit T", AtHardwareLimitTop());
        SmartDashboard.putBoolean("Wrist Stalled", stalled);
        SmartDashboard.putNumber("Wrist Current", motor.getOutputCurrent());
        if (kTuning)
        {
            SmartDashboard.putNumber("Wrist Error", motor.getClosedLoopError(0));
            SmartDashboard.putNumber("Wrist Velocity", motor.getSelectedSensorVelocity(0));
        }
    }

    public boolean AtHardwareLimitBottom()
    {
        return limits.isSet(Limit.REVERSE);
    }

    public boolean AtHardwareLimitTop()
    {
        return limits.isSet(Limit.FORWARD);
    }

    public double getAngle()
    {
        return ticksToDegrees(motor.getSelectedSensorPosition(0));
    }

    /** Wrist encoder has zeroed at any point and is ready to do some bicep curling! */
    public boolean hasZeroed()
    {
        return hasZeroed;
    }

    /** Current sense limit switch is set. Only the top can trigger this. */
    public boolean isStalled()
    {
        return stalled;
    }

    public void setOpenLoop(double percentageUp)
    {
        setMotor(ControlMode.PercentOutput, percentageUp);
    }

    /**
     * Sets the intake position using motion magic
     * @param degrees Bottom: 0, Top: 90, but will try any value
     */
    public void setClosedLoop(double degrees)
    {
        setMotor(ControlMode.MotionMagic, degreesToTicks(degrees));
    }

    public void zeroIfAtLimit()
    {
        if (AtHardwareLimitBottom())
        {
            zeroEncoder();
        }
        else if (AtHardwareLimitTop() || isStalled())
        {
            motor.setSelectedSensorPosition(kTicksToTop, 0, 0);
            hasZeroed = true;
        }
    }

    public void zeroEncoder()
    {
        motor.setSelectedSensorPosition(0, 0, 0);
        hasZeroed = true;
    }

    private double degreesToTicks(double degrees)
    {
        return degrees / kFullRangeDegrees * kTicksToTop;
    }

    private double ticksToDegrees(double ticks)
    {
        return ticks * kFullRangeDegrees / kTicksToTop;
    }

    private void setMotor(ControlMode mode, double setpoint)
    {
        final double feedforward = 0.0;  // TODO Get this working then retune
        motor.set(mode, setpoint, DemandType.ArbitraryFeedForward, feedforward);
    }

    public boolean runFunctionalTest()
    {
        boolean result = true;

        result &= TalonFactory.checkFirmware(motor);
        result &= TalonFactory.checkEncoder(motor);
        // TODO Check limits?

        System.out.println(String.format("Wrist Test: %s", (result) ? "SUCCESS" : "FAILURE"));
        return result;
    }
}
