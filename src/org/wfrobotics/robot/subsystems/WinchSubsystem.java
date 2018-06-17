package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.robot.commands.Winch;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class WinchSubsystem extends Subsystem
{
    private final TalonSRX motor;

    public WinchSubsystem()
    {
        RobotConfig config = RobotConfig.getInstance();
        motor = TalonFactory.makeTalon(config.WINCH);
        TalonFactory.configOpenLoopOnly(motor);
        motor.setInverted(config.WINCH_INVERT);
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
