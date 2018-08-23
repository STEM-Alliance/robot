package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.StallSense;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.SAFMSubsystem;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.wrist.WristAutoZeroThenPercentVoltage;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Wrist extends SAFMSubsystem
{
    private static final double kRangeDegrees = 90.0;
    private final int kTicksToTop;

    private static Wrist instance = null;
    private final RobotState state = RobotState.getInstance();
    private final TalonSRX intakeLift;
    private final StallSense stallSensor;

    private boolean stalled;
    private boolean debugStalledLatched;

    private Wrist()
    {
        RobotConfig config = RobotConfig.getInstance();
        kTicksToTop = config.WRIST_TICKS_TO_TOP;

        intakeLift = TalonFactory.makeClosedLoopTalon(config.WRIST_CLOSED_LOOP).get(0);

        intakeLift.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 10);
        intakeLift.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 10);
        intakeLift.overrideLimitSwitchesEnable(true);
        LimitSwitch.configHardwareLimitAutoZero(intakeLift, true, false);
        intakeLift.setSelectedSensorPosition(0, 0, 10);  // Before zeroing, report values above smart intake active therehold
        intakeLift.configNeutralDeadband(config.WRIST_DEADBAND, 10);
        intakeLift.configOpenloopRamp(.05, 10);

        stallSensor = new StallSense(intakeLift, 4.0, 0.15);
        stalled = false;
        debugStalledLatched = false;
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
        setDefaultCommand(new WristAutoZeroThenPercentVoltage());
    }

    public void updateSensors()
    {
        intakeLift.getSelectedSensorPosition(0);

        stalled = stallSensor.isStalled();
        if (stalled)
        {
            debugStalledLatched = true;
        }

        if (AtTop())
        {
            intakeLift.setSelectedSensorPosition(kTicksToTop, 0, 0);
        }

        state.updateWristPosition(getAngle());
    }

    public void reportState()
    {
        SmartDashboard.putString("Wrist", getCurrentCommandName());
        SmartDashboard.putNumber("Intake Lift Position", intakeLift.getSelectedSensorPosition(0));
        SmartDashboard.putBoolean("Wrist LimitB", AtBottom());
        SmartDashboard.putBoolean("Wrist LimitT", AtTop());
        SmartDashboard.putBoolean("Wrist Stalled", stalled);
        SmartDashboard.putNumber("Wrist Current", intakeLift.getOutputCurrent());
        SmartDashboard.putBoolean("Wrist Stalled Latched", debugStalledLatched);

        // For Calibration
        //        SmartDashboard.putNumber("Intake Lift Error", intakeLift.getClosedLoopError(0));
        //      SmartDashboard.putNumber("Intake Lift Velocity", intakeLift.getSelectedSensorVelocity(0));
    }

    public void setWristSensor(int height)
    {
        intakeLift.setSelectedSensorPosition(height, 0, 0);
    }

    public double getAngle()
    {
        return ticksToDegrees(intakeLift.getSelectedSensorPosition(0));
    }

    public boolean isStalled()
    {
        return stalled;
    }
    public boolean hasStalled()
    {
        return debugStalledLatched;
    }

    public boolean AtBottom()
    {
        return intakeLift.getSensorCollection().isRevLimitSwitchClosed();
    }

    public boolean AtTop()
    {
        return intakeLift.getSensorCollection().isFwdLimitSwitchClosed() || stalled;
    }

    public void setSpeed(double percentageUp)
    {
        intakeLift.set(ControlMode.PercentOutput, percentageUp);
    }

    /**
     * Sets the intake position using motion magic
     * @param percentUp The amount to go up, 0 to 1 for down to up, respectively
     */
    public void setPosition(double percentUp)
    {
        intakeLift.set(ControlMode.MotionMagic, percentUp * kTicksToTop);
    }

    private double ticksToDegrees(double ticks)
    {
        return ticks * kRangeDegrees / kTicksToTop;
    }

    public boolean runFunctionalTest()
    {
        return true;
    }
}
