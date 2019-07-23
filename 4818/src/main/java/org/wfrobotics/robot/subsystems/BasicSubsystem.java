package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.TalonChecker;
import org.wfrobotics.reuse.hardware.TalonFactory;
import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.CommandTemplate;
import org.wfrobotics.robot.config.RobotConfig;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class BasicSubsystem extends EnhancedSubsystem
{
    static class SingletonHolder
    {
        static BasicSubsystem instance = new BasicSubsystem();
    }
    
    Servo test;
    public static BasicSubsystem getInstance()
    {
        return SingletonHolder.instance;
    }

    public BasicSubsystem()
    {
        test = new Servo(0);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new CommandTemplate());
    }

    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {
        SmartDashboard.putString("Intake Command", getCurrentCommandName());
    }

    public TestReport runFunctionalTest()
    {
        TestReport report = new TestReport();

        report.add(getDefaultCommand().doesRequire(this));

        return report;
    }  
}
