package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.Move;
import org.wfrobotics.robot.commands.CommandTemplate;


import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class ArmSubsystem extends EnhancedSubsystem
{
    static class SingletonHolder
    {
        static ArmSubsystem instance = new ArmSubsystem();
    }
    
    Servo one;
    public static ArmSubsystem getInstance()
    {
        return SingletonHolder.instance;
    }

    public ArmSubsystem()
    {
        one = new Servo(0);
    }
    public void moveToAngle(Double angle)
    {
        one.set(angle); 
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new CommandTemplate());
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
