package org.wfrobotics.commands;

import edu.wpi.first.wpilibj.command.Command;

// TODO should we reuse our prior subsystem or make a new one. What type of LEDs this year?

/**
 * Set robot LEDs
 * This command sets the highly visible LEDs mounted on the robot
 * Useful for communication of events to driver or human player, or flaunting after we do something awesome
 * @author drlindne
 *
 */
public class LED extends Command
{
    // TODO Enum for how or why we are setting the LEDs
    
    public LED()
    {
        // TODO We probably want a second constructor that adds a timeout. 
        // Or always require a timeout on one constructor (so if we screw up the command groups, the LEDs aren't always on)???
    }
    
    @Override
    protected void initialize()
    {
        
    }

    @Override
    protected void execute()
    {
        
    }

    @Override
    protected boolean isFinished()
    {
        return isTimedOut();
    }

    @Override
    protected void end()
    {
       
    }

    @Override
    protected void interrupted()
    {
        end();
    }
}
