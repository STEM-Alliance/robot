package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Commands;
import org.wfrobotics.robot.config.IO;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Controls the climber's motion
 * This command controls the climber subsystem motor
 */
public class Up extends Command
{
    public enum MODE {CLIMB, DOWN, VARIABLE_SPEED};

    private final double DEADBAND_TRIGGER = .1;

    private double timeDoneClimbing;
    private MODE mode;

    public Up(MODE mode)
    {
        requires(Robot.climberSubsystem);

        this.mode = mode;
    }

    protected void execute()
    {
        double speed = 0;

        if(mode == MODE.VARIABLE_SPEED)
        {
            speed = IO.getClimbSpeedUp();

            if(speed < DEADBAND_TRIGGER)
            {
                double timeRemaining = DriverStation.getInstance().getMatchTime();

                speed = (timeRemaining < 30 && timeRemaining > 26) ? 1 : 0;
            }
        }
        else if (mode == MODE.CLIMB)
        {
            speed = Commands.CLIMBER_CLIMB_SPEED;

            if (!Robot.climberSubsystem.isAtTop())
            {
                timeDoneClimbing = timeSinceInitialized();
            }
        }
        else if (mode == MODE.DOWN)
        {
            //speed = -1;
        }

        Robot.climberSubsystem.setSpeed(speed);
    }

    protected boolean isFinished()
    {
        if(mode == MODE.CLIMB)
        {
            return timeSinceInitialized() - timeDoneClimbing > Commands.CLIMBER_CLIMB_TIME_AFTER_TOP_REACHED;
        }
        return false;
    }
}
