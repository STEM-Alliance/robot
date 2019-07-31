package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.CommandTemplate;
import org.wfrobotics.robot.commands.ControlledMove;
import org.wfrobotics.robot.commands.JoystickMove;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class ArmSubsystem extends EnhancedSubsystem
{
    static class SingletonHolder
    {
        static ArmSubsystem instance = new ArmSubsystem();
    }
    
    public static ArmSubsystem getInstance()
    {
        return SingletonHolder.instance;
    }
    Servo one;
    Servo two;
    Servo three;
    public ArmSubsystem()
    {
        one = new Servo(0);
        two = new Servo(1);
        three = new Servo(2);
    
        
    }
    public void moveBottom(Double angle)
    {
        one.set(angle); 
    }

    public void moveTop(Double angle)
    {
        two.set(angle); 
    }

    public void moveThree (Double angle)
    {
        three.set(angle);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ControlledMove());
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
    public void cacheSensors(boolean isDisabled)
    {

    }

}
