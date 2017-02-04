package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Move a ball into the shooter, if able.
 * I envision this command being smart enough that it knows if feeding the shooter (and feeder) is okay.
 * @author drlindne
 *
 */
public class Conveyor extends Command 
{
    public enum MODE {SINGLE, CONTINUOUS, OFF};
    
    private final MODE mode;
    private boolean hasFed = false;
    
    public Conveyor(MODE mode)
    {
        requires(Robot.augerSubsystem);
        
        this.mode = mode;
    }
    
    public Conveyor(MODE mode, double timeout)
    {
        requires(Robot.augerSubsystem);
        
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
            Robot.augerSubsystem.setSpeed(0);
        }
        else if (Robot.shooterSubsystem.speedReached(Constants.SHOOTER_READY_SHOOT_SPEED_TOLERANCE))
        {
            // TODO DRL do we care to check the feeder speed? Is this important to make consistent shots?
            Robot.augerSubsystem.setSpeed(.3);
            hasFed = true;
            // TODO DRL do we want to unjam the auger periodically?
        }
        else
        {
            DriverStation.reportError("Conveyor mode not supported", true);
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
        Robot.augerSubsystem.setSpeed(0);
    }

    protected void interrupted()
    {
        end();
    }
}
