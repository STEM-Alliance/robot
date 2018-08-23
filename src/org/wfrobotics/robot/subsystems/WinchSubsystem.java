package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.robot.commands.Winch;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class WinchSubsystem extends Subsystem
{
    private static WinchSubsystem instance = null;
    private final TalonSRX motor;

    private WinchSubsystem()
    {
        RobotConfig config = RobotConfig.getInstance();
        motor = TalonFactory.makeTalon(config.WINCH);
        TalonFactory.configOpenLoopOnly(motor);
        motor.setControlFramePeriod(ControlFrame.Control_3_General, 50);
        // TODO try slow general status
        motor.setInverted(config.WINCH_INVERT);
        motor.setNeutralMode(NeutralMode.Brake);
    }

    public static WinchSubsystem getInstance()
    {
        if (instance == null)
        {
            instance = new WinchSubsystem();
        }
        return instance;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new Winch());
    }

    public void winch(double percentWinch)
    {
        motor.set(ControlMode.PercentOutput, percentWinch);
    }

    public boolean runFunctionalTest()
    {
        boolean result = true;

        result &= TalonFactory.checkFirmware(motor);

        System.out.println(String.format("Winch Test: %s", (result) ? "SUCCESS" : "FAILURE"));
        return result;
    }
}
