package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.wrist.WristOpenLoop;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class Wrist extends EnhancedSubsystem
{
    public static Wrist getInstance()
    {
        if (instance == null)
        {
            instance = new Wrist();
        }
        return instance;
    }
    private static Wrist instance = null;
    private final TalonSRX motor;

    public Wrist()
    {
        final RobotConfig config = RobotConfig.getInstance();

        motor = TalonFactory.makeTalon(config.kAddressTalonWrist);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new WristOpenLoop());
    }

    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {

    }

    public void setSpeed(double percent)
    {
        motor.set(ControlMode.PercentOutput, percent);
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        return report;
    }
}
