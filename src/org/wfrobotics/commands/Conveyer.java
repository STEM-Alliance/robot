package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Move a ball into the shooter, if able.
 * I envision this command being smart enough that it knows if feeding the shooter (and feeder) is okay.
 */
public class Conveyer extends Command 
{
    public enum MODE {SINGLE, CONTINUOUS, UNJAM, OFF};
    public enum UNJAM {JAM, OKAY, UNJAMING};
    
    private final double LASTBALL_INVALID = -1;
    
    private final MODE mode;
    private UNJAM unjam;
    private boolean hasFed = false;
    private double lastBall;
    private double unjamStart;
    private boolean stillUnjaming;
    private double augerSpeed;
    
    public Conveyer(MODE mode)
    {
        requires(Robot.augerSubsystem);
        
        unjam = UNJAM.OKAY;
        this.mode = mode;
    }
     
    public Conveyer(MODE mode, double timeout)
    {
        requires(Robot.augerSubsystem);
        
        this.mode = mode;
        unjam = UNJAM.OKAY;
        lastBall = -1;

        setTimeout(timeout);
    }
    
    protected void initialize()
    {
        lastBall = LASTBALL_INVALID;
    }

    protected void execute()
    {
        Utilities.PrintCommand("Conveyer", this, mode.toString());
        
        if (mode == MODE.OFF)
        {
            Robot.augerSubsystem.setSpeed(0);
        }
        else if(mode == MODE.UNJAM)
        {
            Robot.augerSubsystem.setSpeed(Constants.AUGER_UNJAM_SPEED);
        }
        else 
        {
            if (Robot.shooterSubsystem.topSpeedReached(Constants.SHOOTER_READY_SHOOT_SPEED_TOLERANCE))
            {
                if (lastBall == LASTBALL_INVALID)
                {
                    lastBall = timeSinceInitialized();  // Reset the timer the first time so we don't start in unjamming mode
                }
                
                // Default is NOT_JAM
                if (unjam == UNJAM.OKAY)
                {
                    augerSpeed = Constants.AUGER_SPEED;
                    unjamStart = timeSinceInitialized();  
                    
                    if(isJamed())
                    {
                        unjam = UNJAM.UNJAMING;                        
                    }
                }
                if (unjam == UNJAM.UNJAMING)
                {
                    stillUnjaming = timeSinceInitialized() - unjamStart < Constants.AUGER_UNJAMING_TIME;
                    if(stillUnjaming)
                    {            
                        augerSpeed = Constants.AUGER_UNJAM_SPEED;
                    }
                    else
                    {
                        unjam = UNJAM.OKAY;
                    }
                }
                Robot.augerSubsystem.setSpeed(augerSpeed);
                // TODO DRL do we care to check the feeder speed? Is this important to make consistent shots?            
            }
        }
    }
    
    public boolean isJamed()
    {        
        if ( Constants.SHOOTER_READY_SHOOT_SPEED - Robot.shooterSubsystem.getSpeedTop() > Constants.SHOOTER_TRIGGER_SPEED_DROP )
        {
            lastBall = timeSinceInitialized();
            hasFed = true;
        }        
        return (timeSinceInitialized() - lastBall > Constants.AUGER_TIME_SINCE_BALL);
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
