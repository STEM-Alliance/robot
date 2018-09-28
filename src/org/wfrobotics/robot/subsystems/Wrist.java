package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.LimitSwitch.Limit;
import org.wfrobotics.reuse.hardware.StallSense;
import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.wrist.WristZeroThenOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The wrist consists of a BAG motor to rotate the intake
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class Wrist extends EnhancedSubsystem
{
    static class SingletonHolder
    {
        static Wrist instance = new Wrist();
    }

    public static Wrist getInstance()
    {
        return SingletonHolder.instance;
    }

    private static final double kFullRangeDegrees = 90.0;
    private final int kTicksToTop;
    private final boolean kTuning;

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
        kTuning = config.kWinchTuning;

        motor = TalonFactory.makeClosedLoopTalon(config.WRIST_CLOSED_LOOP).get(0);
        motor.setSelectedSensorPosition(kTicksToTop, 0, 100);  // Start able to always reach limit switch
        motor.configNeutralDeadband(config.kWristDeadband, 100);
        motor.configOpenloopRamp(.1, 10);
        motor.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_10Ms, 100);
        motor.configVelocityMeasurementWindow(1, 100);
        TalonFactory.configCurrentLimiting(motor, 20, 40, 200);  // Adding with high numbers just in case
        motor.setControlFramePeriod(ControlFrame.Control_3_General, 100);  // Slow down, wrist responsiveness not critical
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 5, 100);  // Faster limit switches
        motor.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 20, 100);  // Slow down, doesn't make decisions off this
        TalonFactory.configFastErrorReporting(motor, kTuning);
        // TODO Try configAllowableClosedloopError()
        // TODO Try using Status_10_MotionMagic to improve motion?

        limits = new LimitSwitch(motor, LimitSwitchNormal.NormallyOpen, LimitSwitchNormal.NormallyOpen);
        LimitSwitch.configSoftwareLimitF(motor, kTicksToTop, true);
        LimitSwitch.configSoftwareLimitR(motor, -500, true);
        LimitSwitch.configHardwareLimitAutoZero(motor, false, false);

        stallSensor = new StallSense(motor, 25.0, 0.1);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristZeroThenOpenLoop());
    }

    public void cacheSensors(boolean isDisabled)
    {
        stalled = stallSensor.isStalled();
        zeroIfAtLimit();
        state.updateWrist(getAngle());
    }

    public void reportState()
    {
        SmartDashboard.putNumber("Wrist Position", motor.getSelectedSensorPosition(0));
        SmartDashboard.putNumber("Wrist Velocity", motor.getSelectedSensorVelocity(0));
        SmartDashboard.putBoolean("Wrist Limit B", AtHardwareLimitBottom());
        SmartDashboard.putBoolean("Wrist Limit T", AtHardwareLimitTop());
        SmartDashboard.putBoolean("Wrist Stalled", stalled);
        SmartDashboard.putNumber("Wrist Current", motor.getOutputCurrent());
        if (kTuning)
        {
            SmartDashboard.putNumber("Wrist Error", motor.getClosedLoopError(0));
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

        result &= getDefaultCommand().doesRequire(this);
        result &= TalonChecker.checkFirmware(motor);
        result &= TalonChecker.checkEncoder(motor);
        result &= TalonChecker.checkFrameRates(motor);

        int retries = 10;
        while (!hasZeroed && retries-- > 0)
        {
            setOpenLoop(-.3);
            Timer.delay(.2);
            setOpenLoop(0.0);
        }
        result &= AtHardwareLimitBottom();
        result &= TalonChecker.checkSensorPhase(0.3, motor);

        return result;
    }
}
