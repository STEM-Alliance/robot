package org.wfrobotics.commands;

import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Move a ball into the shooter, if able.
 * I envision this command being smart enough that it knows if feeding the shooter (and feeder) is okay
 */
public class Conveyor extends Command
{
    public enum MODE {CONTINUOUS, UNJAM, ON_HOLD, OFF};

    private final MODE mode;
    private double timeStartPeriod;
    private boolean unjamming;

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
        timeStartPeriod = timeSinceInitialized();
        unjamming = false;
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
            Robot.augerSubsystem.setSpeed(Constants.AUGER_SPEED - OI.getAugerSpeedAdjust()*.2);
        }
        else 
        {
            double speed = (unjamming) ? Constants.AUGER_UNJAM_SPEED:Constants.AUGER_SPEED;

            SmartDashboard.putNumber("AugerSpeed", speed);
            Robot.augerSubsystem.setSpeed(speed);

            if (timeSinceInitialized() - timeStartPeriod > Constants.AUGER_NORMAL_PERIOD + Constants.AUGER_UNJAM_PERIOD)
            {
                unjamming = false;
                timeStartPeriod = timeSinceInitialized();
            }
            else if (timeSinceInitialized() - timeStartPeriod > Constants.AUGER_NORMAL_PERIOD)
            {
                unjamming = true;
            }
        }
    }

    protected boolean isFinished()
    {
        boolean finished;

        if (mode == MODE.OFF)
        {
            finished = false;
        }
        else if (mode == MODE.CONTINUOUS)
        {
            finished = isTimedOut();
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
