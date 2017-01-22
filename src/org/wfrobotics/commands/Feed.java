package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Feed a ball into the shooter, if able.
 * I envision this command being smart enough that it knows if feeding the shooter is okay.
 * @author drlindne
 *
 */
public class Feed extends Command 
{
    public enum MODE {SINGLE, CONTINUOUS, OFF};
    
    private final MODE mode;
    private boolean hasFed = false;
    
    public Feed(MODE mode)
    {
        requires(Robot.feederSubsystem);
        
        this.mode = mode;
    }
    
    public Feed(MODE mode, double timeout)
    {
        requires(Robot.feederSubsystem);
        
        this.mode = mode;
        setTimeout(timeout);
    }
    
    protected void initialize()
    {
        
    }

    protected void execute()
    {
        if (mode == MODE.OFF)
        {
            Robot.feederSubsystem.feed(false);
        }
        else if (Robot.shooterSubsystem.speedReached(Constants.SHOOTER_READY_SHOOT_SPEED_TOLERANCE))
        {
            Robot.feederSubsystem.feed(true);  // TODO DRL do we need to reset the feeder?
            hasFed = true;
        }
    }

    protected boolean isFinished()
    {
        boolean finished;
        
        if (mode == MODE.OFF)
        {
            finished = false;
        }
        else if (mode == MODE.SINGLE)
        {
            finished = hasFed && !isTimedOut();
        }
        else if (mode == MODE.CONTINUOUS)
        {
            finished = isTimedOut();
        }            
        else
        {
            finished = true;
        }
        
        return finished;
    }

    protected void end()
    {
        Robot.feederSubsystem.feed(false);
    }

    protected void interrupted()
    {
        end();
    }
}
