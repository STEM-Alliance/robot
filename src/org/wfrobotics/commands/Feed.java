package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Feed the shooter
 * Set the motor speed which accelerates balls into the shooter.
 * @author drlindne
 *
 */
public class Feed extends Command 
{    
    public enum MODE {FEED, OFF};  // TODO DRL Unjam?
    
    private final MODE mode;
    
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

    @Override
    protected void initialize()
    {
        
    }

    @Override
    protected void execute()
    {
        if (mode == MODE.FEED)
        {
            Robot.feederSubsystem.setSpeed(Constants.SHOOTER_READY_SHOOT_SPEED);
        }
        else if (mode == MODE.OFF)
        {
            Robot.feederSubsystem.setSpeed(0);
        }
        else
        {
            DriverStation.reportError("Feed mode not supported", true);
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
        // If you need to shut off the motors, probably create a new command or set the subsystem in your group's end()???
    }

    @Override
    protected void interrupted()
    {
        end();
    }
}
