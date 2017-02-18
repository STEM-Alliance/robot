package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Move a ball into the shooter, if able.
 * I envision this command being smart enough that it knows if feeding the shooter (and feeder) is okay.
 * @author drlindne
 *
 */
public class Conveyor extends Command 
{
    public enum MODE {SINGLE, CONTINUOUS, UNJAM, ON_HOLD, OFF};
    
    private final MODE mode;
    private boolean hasFed = false;
    private double lastBall;
    private double unjamTime;
    private boolean unjaming;
    private boolean timeoutMode = false;
    
    public Conveyor(MODE mode)
    {
        requires(Robot.augerSubsystem);
        
        this.mode = mode;
        timeoutMode = false;
    }
    
    public Conveyor(MODE mode, double timeout)
    {
        requires(Robot.augerSubsystem);
        
        this.mode = mode;
        setTimeout(timeout);
        timeoutMode = true;
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
            Robot.augerSubsystem.setSpeed(Constants.AUGER_UNJAM_SPEED);
        }
        else if(mode == MODE.ON_HOLD)
        {
            Robot.augerSubsystem.setSpeed(Constants.AUGER_SPEED);
        }
        else 
        {
            double augerSpeed = 0;
            
            if (Robot.shooterSubsystem.bothInTolerance(Constants.SHOOTER_READY_SHOOT_SPEED_TOLERANCE_RPM))
            {
                SmartDashboard.putBoolean("ShooterReady", true);
                // TODO DRL do we care to check the feeder speed? Is this important to make consistent shots?
                augerSpeed = Constants.AUGER_SPEED;
                if(!isJamed() && !unjaming )
                {
                    unjamTime = timeSinceInitialized();
                    hasFed = true;
                }
                else
                {  
                    unjaming = timeSinceInitialized() - unjamTime < Constants.AUGER_UNJAMING_TIME;
                    if(unjaming)
                    {
                        augerSpeed = Constants.AUGER_UNJAM_SPEED;
                    }
                }
            }
            else
            {
                SmartDashboard.putBoolean("ShooterReady", false);
            }

            SmartDashboard.putNumber("AugerSpeed", augerSpeed);
            Robot.augerSubsystem.setSpeed(augerSpeed);
        }
    }
    
    public boolean isJamed()
    {        
        if ( Constants.SHOOTER_READY_SHOOT_SPEED - Robot.shooterSubsystem.getSpeedTop() > Constants.SHOOTER_TRIGGER_SPEED_DROP )
        {
            lastBall = timeSinceInitialized();
        }        
        SmartDashboard.putNumber("UnJamTime", timeSinceInitialized() - lastBall);
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
            if(timeoutMode)
            {
                finished = isTimedOut();
            }
            else
            {
                finished = false;
            }
        }
        else if (mode == MODE.ON_HOLD || mode == MODE.UNJAM)
        {
            finished = false;
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
