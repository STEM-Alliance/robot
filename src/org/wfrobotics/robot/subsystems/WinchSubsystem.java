package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.commands.Winch;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class WinchSubsystem extends Subsystem
{
    private final TalonSRX motor;

    public WinchSubsystem(RobotConfig config)
    {
        motor = TalonSRXFactory.makeTalon(Robot.config.WINCH);
        TalonSRXFactory.configOpenLoopOnly(motor);
        motor.setInverted(Robot.config.WINCH_INVERT);
        motor.setNeutralMode(NeutralMode.Brake);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new Winch());
    }

    public void winch(double percentWinch)
    {
        motor.set(ControlMode.PercentOutput, percentWinch);
    }
}
