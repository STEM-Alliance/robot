package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.WinchOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * The winch consists of one Mini CIM motor to pull up the robot after our hook is deployed
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 */
public class Winch extends EnhancedSubsystem
{
    static class SingletonHolder
    {
        static Winch instance = new Winch();
    }

    public static Winch getInstance()
    {
        return SingletonHolder.instance;
    }

    // private final TalonSRX motor;

    private Winch()
    {
        RobotConfig config = RobotConfig.getInstance();
        // motor = TalonFactory.makeTalon(config.kWinchAddress);
        // motor.setInverted(config.kWinchInvert);
        // motor.setNeutralMode(NeutralMode.Brake);
        // TalonFactory.configCurrentLimiting(motor, 25, 35, 200);  // TODO Added this, is okay?
        // TalonFactory.configOpenLoopOnly(motor);
        // motor.setControlFramePeriod(ControlFrame.Control_3_General, 50);  // Responsiveness non-critical
        // motor.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General, 20, 100);  // TODO Okay to slow down?
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WinchOpenLoop());
    }

    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {

    }

    public void winch(double percentWinch)
    {
        //motor.set(ControlMode.PercentOutput, percentWinch);
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));
        //report.add(TalonChecker.checkFirmware(motor));
        //report.add(TalonChecker.checkFrameRates(motor), false);  // intentionally slow rates

        return report;
    }
}
