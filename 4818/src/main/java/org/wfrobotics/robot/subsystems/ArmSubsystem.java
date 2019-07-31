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
    Servo base;
    Servo reach;
    Servo lift;
    Servo claw;
    public ArmSubsystem()
    {
        base = new Servo(0);
        reach = new Servo(1);
        lift = new Servo(2);
        claw = new Servo(3);
    
        
    }
    public void rotateBase(Double angle)
    {
        base.set(angle); 
    }

    public void reach(Double angle)
    {
        reach.set(angle); 
    }

    public void lift (Double angle)
    {
        lift.set(angle);
    }

    public void pinch (double angle)
    {
        claw.set(angle);
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
