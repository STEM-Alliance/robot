package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 * Controls the climber's motion
 * This command controls the climber subsystem motor
 * @author drlindne
 *
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
        if(this.mode == MODE.CLIMB)
        {
            //is the robot at the top?
            if (!Robot.climberSubsystem.isAtTop())
            {
                time = this.timeSinceInitialized();
                Robot.climberSubsystem.setSpeed(1);
            }     
            //TODO: The buffer is currently set to 0.5 seconds THIS NEEDS TO BE TESTED!! 
            else
            {
                if(this.timeSinceInitialized() - time <= 0.5)
                {
                    Robot.climberSubsystem.setSpeed(1);
                }
                else
                {
                    mode = MODE.OFF;
                }
            }
        }
        else if(this.mode == MODE.DOWN)
        {
            Robot.climberSubsystem.setSpeed(-1);
        }
        else if(this.mode == MODE.OFF)
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
