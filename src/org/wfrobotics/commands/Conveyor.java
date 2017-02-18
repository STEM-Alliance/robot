package org.wfrobotics.commands;

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
            double nextPeriod = timeStartPeriod + Constants.AUGER_UNJAM_PERIOD;
            double nextUnjam = nextPeriod * (1 - Constants.AUGER_UNJAM_DUTYCYCLE);
            double now = timeSinceInitialized();
            double speed = (now > nextUnjam) ? Constants.AUGER_SPEED:Constants.AUGER_UNJAM_SPEED;

            if (now > nextPeriod)
            {
                timeStartPeriod = now;
            }

            SmartDashboard.putNumber("AugerSpeed", speed);
            Robot.augerSubsystem.setSpeed(speed);
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
