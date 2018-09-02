package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.utilities.Testable;
import org.wfrobotics.robot.commands.Winch;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * The winch consists of one Mini CIM motor to pull up the robot after our hook is deployed
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class WinchSubsystem extends Subsystem implements Testable
{
    private static WinchSubsystem instance = null;
    private final TalonSRX motor;

    private WinchSubsystem()
    {
        RobotConfig config = RobotConfig.getInstance();
        motor = TalonFactory.makeTalon(config.kWinchAddress);
        motor.setInverted(config.kWinchInvert);
        motor.setNeutralMode(NeutralMode.Brake);
        TalonFactory.configCurrentLimiting(motor, 25, 35, 200);  // TODO Added this, is okay?
        TalonFactory.configOpenLoopOnly(motor);
        motor.setControlFramePeriod(ControlFrame.Control_3_General, 50);  // Responsiveness non-critical
        motor.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 20, 100);  // TODO Okay to slow down?
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

    public boolean runFunctionalTest(boolean includeMotion)
    {
        boolean result = true;

        System.out.println("Winch Test:");
        result &= TalonChecker.checkFirmware(motor);
        TalonChecker.checkFrameRates(motor);  // Not in result, intentionally slow rates

        System.out.println(String.format("Winch Test: %s", (result) ? "SUCCESS" : "FAILURE"));
        return result;
    }
}
