package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.EnhancedSubsystem;
import org.wfrobotics.robot.commands.ExampleStopCommand;

/** TODO: comment what this Subsystem does */
// TODO rename me (right click -> refactor -> rename)
public class ExampleSubsystem extends EnhancedSubsystem
{
    // TODO create any hardware this Subsystem needs to control the robot

    public ExampleSubsystem()
    {
        // TODO setup any hardware this Subsystem will need
    }

    protected void initDefaultCommand()
    {
        // TODO pick which Command runs whenever buttons are not being pressed
        setDefaultCommand(new ExampleStopCommand());
    }

    public void cacheSensors(boolean isDisabled)
    {

    }

    public void reportState()
    {

    }

    // TODO rename me (right click -> refactor -> rename)
    public void setSpeed(int percentForward)  // TODO pass any parameters needed to each method
    {
        // TODO create any methods needed to control this Subsystem, called by Commands
    }

    public TestReport runFunctionalTest()
    {
        return new TestReport();
    }
}