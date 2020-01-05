package org.wfrobotics.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.ExampleStopCommand;


/** TODO: comment what this Subsystem does */
// TODO rename me (right click -> refactor -> rename)
public class ExampleSubsystem extends EnhancedSubsystem
{
    // TODO create any hardware this Subsystem needs to control the robot

    TalonSRX motor1;

    public ExampleSubsystem()
    {
        // TODO setup any hardware this Subsystem will need
        motor1 = new TalonSRX(18);
    }

    protected void initDefaultCommand()
    {
        // TODO pick which Command runs whenever buttons are not being pressed
        setDefaultCommand(new ExampleStopCommand());
    }

    // TODO rename me (right click -> refactor -> rename)
    public void setSpeed(double percentForward)  // TODO pass any parameters needed to each method
    {
         motor1.set(ControlMode.PercentOutput, Math.abs(percentForward));
    }
    
    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {
        
    }

    public TestReport runFunctionalTest()
    {
        return new TestReport();
    }
    
    /**
     * 
     *  The code below is the control that needs to be put in every subsystem to ensure that there 
     *  is only one version of the subsystem running on the robot at one time!
     * 
     */
    static class SingletonHolder
    {
        static ExampleSubsystem instance = new ExampleSubsystem();
    }

    public static ExampleSubsystem getInstance()
    {
        return SingletonHolder.instance;
    }
}