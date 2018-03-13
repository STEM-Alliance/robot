package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.commands.wrist.IntakeLiftAutoZeroThenPercentVoltage;
import org.wfrobotics.robot.config.RobotMap;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Wrist extends Subsystem
{
    private final TalonSRX intakeLift;

    public Wrist(RobotConfig config)
    {
        intakeLift = TalonSRXFactory.makeConstAccelControlTalon(RobotMap.CAN_INTAKE_LIFT, config.INTAKE_P, config.INTAKE_I, config.INTAKE_D, config.INTAKE_F, 0, config.INTAKE_MAX_POSSIBLE_UP, config.INTAKE_ACCELERATION);
        intakeLift.set(ControlMode.PercentOutput, 0);
        intakeLift.setNeutralMode(NeutralMode.Brake);
        intakeLift.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 10);
        intakeLift.configForwardSoftLimitThreshold(config.INTAKE_TICKS_TO_TOP, 10);
        intakeLift.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 10);
        intakeLift.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, 10);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new IntakeLiftAutoZeroThenPercentVoltage());
        //        setDefaultCommand(new IntakeLift());
    }

    public boolean intakeLiftAtBottom()
    {
        return intakeLift.getSensorCollection().isRevLimitSwitchClosed();
    }

    public void setIntakeLiftSpeed(double percentageUp)
    {
        intakeLift.set(ControlMode.PercentOutput, percentageUp);
    }

    /**
     * sets the intake position using motion magic
     * @param percentUp The amount to go up, 0 to 1 for down to up, respectively
     */
    public void setIntakeLiftPosition(double percentUp)
    {
        intakeLift.set(ControlMode.MotionMagic, percentUp * Robot.config.INTAKE_TICKS_TO_TOP);
    }

    public void ZeroIntakeEncoder()
    {
        intakeLift.setSelectedSensorPosition(0, 0, 10);
    }

    public void reportState()
    {
        SmartDashboard.putNumber("Intake Lift Encoder", intakeLift.getSelectedSensorPosition(0));
        SmartDashboard.putNumber("Intake Lift Velocity", intakeLift.getSelectedSensorVelocity(0));
    }
}
