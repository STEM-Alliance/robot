package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.commands.Winch;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WinchSubsystem extends Subsystem
{
    private final TalonSRX motor;

    public WinchSubsystem()
    {
        motor = TalonSRXFactory.makeTalon(Robot.config.WINCH);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1000, 0);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 1000, 0);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, 1000, 0);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 1000, 0);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 1000, 0);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 1000, 0);

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
