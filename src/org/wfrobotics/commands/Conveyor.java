package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Move a ball into the shooter, if able.
 * I envision this command being smart enough that it knows if feeding the shooter (and feeder) is okay.
 * @author drlindne
 *
 */
public class Conveyor extends Command 
{
    public enum MODE {SINGLE, CONTINUOUS, UNJAM, OFF};
    
    private final MODE mode;
    private boolean hasFed = false;
    private double lastBall;
    private double unjamTime;
    private boolean unjaming;
    
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
        else if(mode == MODE.UNJAM)
        {
            Robot.augerSubsystem.setSpeed(-0.4);
        }
        else 
        {
            double augerSpeed = 0;
            
            if (Robot.shooterSubsystem.bothInTolerance(Constants.SHOOTER_READY_SHOOT_SPEED_TOLERANCE))
            {
                // TODO DRL do we care to check the feeder speed? Is this important to make consistent shots?
                augerSpeed = .3;
                if(!isJamed() && !unjaming )
                {
                    unjamTime = timeSinceInitialized();
                    hasFed = true;
                }
                else
                {  
                    unjaming = timeSinceInitialized() - unjamTime < 2;
                    if(unjaming)
                    {
                        augerSpeed = -0.3;
                    }
                }
            }
            
            Robot.augerSubsystem.setSpeed(augerSpeed);
        }
    }
    
    public boolean isJamed()
    {        
        if ( Constants.SHOOTER_READY_SHOOT_SPEED - Robot.shooterSubsystem.getSpeedTop() > Constants.SHOOTER_TRIGGER_SPEED_DROP )
        {
            lastBall = timeSinceInitialized();
        }        
        return (timeSinceInitialized() - lastBall > 5);
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
