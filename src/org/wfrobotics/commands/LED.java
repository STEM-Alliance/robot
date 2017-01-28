package org.wfrobotics.commands;

import org.wfrobotics.commands.Feed.MODE;
import org.wfrobotics.led.Effect;
import org.wfrobotics.led.Hardware;
import org.wfrobotics.led.LEDs;
import org.wfrobotics.led.Effect.EFFECT;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import javafx.scene.paint.Color;

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
    public enum MODE { BLINK, SOLID, OFF};
    private final MODE mode;
    public boolean isOn;    
    
    public LED(MODE mode)
    {
        // TODO We probably want a second constructor that adds a timeout. 
        // Or always require a timeout on one constructor (so if we screw up the command groups, the LEDs aren't always on)???
        this.mode = mode;
        requires(Robot.ledSubsystem);

    }
    public LED(MODE mode, double timeout)
    {
        this.mode = mode;
        setTimeout(timeout);
        requires(Robot.ledSubsystem);
    }
    @Override
    protected void initialize()
    {
        
    }

    @Override
    protected void execute()
    {
        timeSinceInitialized();
    }
    public void ledOn()
    {
        Robot.ledSubsystem.setOn(true, org.wfrobotics.subsystems.Led.MODE.TOP);
    }
    
    public void ledBlink()
    {
        double time = Math.floor(timeSinceInitialized());
        if (time % 2 == 0)
        {
            Robot.ledSubsystem.setOn(true, org.wfrobotics.subsystems.Led.MODE.TOP);
        }
        else
        {
            Robot.ledSubsystem.setOn(false, org.wfrobotics.subsystems.Led.MODE.TOP);
        }
    }
    public void ledBlink(int durration)
    {
        double time = Math.floor(timeSinceInitialized());
        if (time % 2 == 0)
        {
            Robot.ledSubsystem.setOn(true, org.wfrobotics.subsystems.Led.MODE.TOP);
        }
        else
        {
            Robot.ledSubsystem.setOn(false, org.wfrobotics.subsystems.Led.MODE.TOP);
        }
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
