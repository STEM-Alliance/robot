package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Commands;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This command rev's the shooters motor.
 * This may be useful by itself in situations when you anticipate the need to shoot, reducing the setup time.
 * If another command tries to use the shooter subsystem, I envision this command ending.
 *
 */
public class Rev extends Command
{
    public enum MODE {SHOOT, RAMP, OFF, FORCE_OFF};

    private final MODE mode;
    private int consecutiveSamplesAtSpeed;

    public Rev(MODE mode)
    {
        requires(Robot.shooterSubsystem);

        this.mode = mode;
    }

    public Rev(MODE mode, double timeout)
    {
        requires(Robot.shooterSubsystem);

        this.mode = mode;
        consecutiveSamplesAtSpeed = 0;
        setTimeout(timeout);
    }

    protected void execute()
    {
        if (mode == MODE.OFF)
        {
            Robot.shooterSubsystem.topThenBottom(0,  Commands.SHOOTER_READY_SHOOT_SPEED_TOLERANCE_RPM);
        }
        else if (mode == MODE.SHOOT || mode == MODE.RAMP)
        {
            Robot.shooterSubsystem.topThenBottom(Commands.SHOOTER_READY_SHOOT_SPEED, Commands.SHOOTER_READY_SHOOT_SPEED_TOLERANCE_RPM);

            if (Robot.shooterSubsystem.inTolerance(Commands.SHOOTER_READY_SHOOT_SPEED_TOLERANCE_RPM))
            {
                consecutiveSamplesAtSpeed++;
            }
            else
            {
                consecutiveSamplesAtSpeed = 0;
            }
        }
        else if(mode == MODE.FORCE_OFF)
        {
            Robot.shooterSubsystem.forceOff();
        }
        else
        {
            Robot.shooterSubsystem.topThenBottom(0,  Commands.SHOOTER_READY_SHOOT_SPEED_TOLERANCE_RPM);
        }
    }

    protected boolean isFinished()
    {
        if (mode == MODE.RAMP)
        {
            return isTimedOut() || consecutiveSamplesAtSpeed > Commands.SHOOTER_READY_CONSECUTIVE_SAMPLES;
        }
        return isTimedOut();
    }

    protected void end()
    {
        // If you need to shut off the motors, probably create a new command or set the subsystem in your group's end()???
    }
}
