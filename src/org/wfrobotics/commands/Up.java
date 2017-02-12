package org.wfrobotics.commands;

import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Controls the climber's motion
 * This command controls the climber subsystem motor
 */
public class Up extends Command
{
    public enum MODE {CLIMB, DOWN, OFF};
    
    private double time;
    private MODE mode;
    
    public Up(MODE mode)
    {
        requires(Robot.climberSubsystem);
        
        this.mode = mode;
    }
    
    @Override
    protected void initialize()
    {

    }

    @Override
    protected void execute()
    {
        if(mode == MODE.OFF)
        {
            Robot.climberSubsystem.setSpeed(0);
        }
        else if (mode == MODE.CLIMB)
        {
            if (!Robot.climberSubsystem.isAtTop())
            {
                time = timeSinceInitialized();
                Robot.climberSubsystem.setSpeed(OI.getClimbSpeed());
            }
            else
            {
                if(timeSinceInitialized() - time < Constants.CLIMBER_CLIMB_TIME_AFTER_TOP_REACHED)
                {
                    Robot.climberSubsystem.setSpeed(OI.getClimbSpeed());
                }
                else
                {
                    mode = MODE.OFF;
                }
            }
        }
        else if (mode == MODE.DOWN)
        {
            Robot.climberSubsystem.setSpeed(-OI.getClimbSpeed());
        }
        else
        {
            Robot.climberSubsystem.setSpeed(0);
        }
    }

    @Override
    protected boolean isFinished()
    {
        if(this.mode == MODE.CLIMB)
        {
            return Robot.climberSubsystem.isAtTop();
        }
        else
        {
            return false;
        }
    }

    @Override
    protected void end()
    {
        //Not sure if we should turn off the motor; Robot might fall?
    }

    @Override
    protected void interrupted()
    {

    }
}
