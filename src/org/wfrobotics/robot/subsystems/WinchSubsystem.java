package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.commands.Winch;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WinchSubsystem extends Subsystem
{
    private final TalonSRX motor;

    public WinchSubsystem()
    {
        motor = new TalonSRX(Robot.config.WINCH);
        motor.setInverted(Robot.config.WINCH_INVERT);
        motor.setNeutralMode(NeutralMode.Brake);
        // TODO Try current control, limits, etc
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new Winch());
    }

    public void winch(double percentWinch)
    {
        motor.set(ControlMode.PercentOutput, percentWinch);
        SmartDashboard.putNumber("Winch (A)", motor.getOutputCurrent());
    }
}
