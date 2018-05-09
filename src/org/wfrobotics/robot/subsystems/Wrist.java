package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.LimitSwitch;
import org.wfrobotics.reuse.hardware.StallSensor;
import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.commands.wrist.WristAutoZeroThenPercentVoltage;
import org.wfrobotics.robot.config.RobotMap;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Wrist extends Subsystem
{
    private final RobotState state = RobotState.getInstance();
    private final TalonSRX intakeLift;
    private final StallSensor stallSensor;

    private boolean stalled;
    private boolean debugStalledLatched;

    public Wrist(RobotConfig config)
    {
        intakeLift = TalonSRXFactory.makeMotionMagicTalon(RobotMap.CAN_INTAKE_LIFT, config.WRIST_GAINS, config.WRIST_MAX_POSSIBLE_UP, config.WRIST_ACCELERATION);
        intakeLift.setNeutralMode(NeutralMode.Brake);
        intakeLift.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 10);
        intakeLift.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 10);
        intakeLift.overrideLimitSwitchesEnable(true);
        LimitSwitch.configHardwareAutoZero(intakeLift, true, false);
        intakeLift.setSelectedSensorPosition(0, 0, 10);  // Before zeroing, report values above smart intake active therehold
        intakeLift.configOpenloopRamp(.05, 10);

        stallSensor = new StallSensor(intakeLift, 15.0, 0.15);
        stalled = false;
        debugStalledLatched = false;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristAutoZeroThenPercentVoltage());
    }

    public void updateSensors()
    {
        double position = intakeLift.getSelectedSensorPosition(0);

        stalled = stallSensor.isStalled();
        if (stalled)
        {
            debugStalledLatched = true;
        }

        if (AtTop())
        {
            intakeLift.setSelectedSensorPosition(Robot.config.WRIST_TICKS_TO_TOP, 0, 0);
        }

        state.updateWristPosition(position);
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
        intakeLift.set(ControlMode.MotionMagic, percentUp * Robot.config.WRIST_TICKS_TO_TOP);
    }
}
