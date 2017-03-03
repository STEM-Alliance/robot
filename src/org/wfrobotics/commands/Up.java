package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Controls the climber's motion
 * This command controls the climber subsystem motor
 */
public class Up extends Command
{
    public enum MODE {AUTOCLIMB, OFF, CLIMB, DOWN, VARIABLE_SPEED};

    private final double DEADBAND_TRIGGER = .1; 

    private double timeDoneClimbing;
    private MODE mode;

    public Up(MODE mode)
    {
        requires(Robot.climberSubsystem);

        this.mode = mode;
    }

    @Override
    protected void execute()
    {
        double speed = 0;
        
        Utilities.PrintCommand("Up", this, mode.toString());
        if(mode == MODE.AUTOCLIMB)
        {
            double timeRemaining = DriverStation.getInstance().getMatchTime();
            
            speed = (timeRemaining < 30) ? Constants.CLIMBER_CLIMB_SPEED : 0;
        }        
        else if (mode == MODE.OFF)
        {
            speed = 0;
        }
        else if (mode == MODE.VARIABLE_SPEED)
        {
            speed = OI.getClimbSpeedUp();

            if(speed < DEADBAND_TRIGGER)
            {
                double timeRemaining = DriverStation.getInstance().getMatchTime();

                speed = (timeRemaining < 30 && timeRemaining > 26) ? 1 : 0;
            }
        }
        else if (mode == MODE.CLIMB)
        {
            speed = Constants.CLIMBER_CLIMB_SPEED;
            
            if (!Robot.climberSubsystem.isAtTop())
            {
                timeDoneClimbing = timeSinceInitialized();
            }
        }
        else if (mode == MODE.DOWN)
        {
            speed = -1;
        }

        SmartDashboard.putNumber("ClimbSpeed", speed);
        Robot.climberSubsystem.setSpeed(speed);
    }

    @Override
    protected boolean isFinished()
    {
        if(this.mode == MODE.CLIMB)
        {
            return timeSinceInitialized() - timeDoneClimbing > Constants.CLIMBER_CLIMB_TIME_AFTER_TOP_REACHED;
        }
        else
        {
            return false;
        }
    }
}
