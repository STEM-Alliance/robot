package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonSRXFactory;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.commands.Winch;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class WinchSubsystem extends Subsystem
{
    private final boolean downNotSafe;

    private final TalonSRX motor;

    public WinchSubsystem(RobotConfig config)
    {
        motor = TalonSRXFactory.makeTalon(Robot.config.WINCH);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 1000, 10);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, 1000, 10);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, 1000, 10);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth, 1000, 10);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 1000, 10);
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 1000, 10);

        motor.setInverted(Robot.config.WINCH_INVERT);
        motor.setNeutralMode(NeutralMode.Brake);
        // TODO Try current control, limits, etc

        downNotSafe = !config.WINCH_DOWN_IS_SAFE;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new Winch());
    }

    public void winch(double percentWinch)
    {
        motor.set(ControlMode.PercentOutput, (downNotSafe && percentWinch < 0) ?  0 : percentWinch);
    }
}
