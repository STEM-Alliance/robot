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
        Utilities.PrintCommand("Up", this, mode.toString());
        if(mode == MODE.OFF)
        {
            double up = OI.getClimbSpeedUp();

            SmartDashboard.putNumber("ClimbSpeed", up);
            
            if(up > .1)
            {
                Robot.climberSubsystem.setSpeed(up);
            }
            else
            {
                Robot.climberSubsystem.setSpeed(0);
            }

            if(DriverStation.getInstance().getMatchTime() < 30
                    && 
                    DriverStation.getInstance().getMatchTime() >29
                    && !(DriverStation.getInstance().isAutonomous()))
            {
                Robot.climberSubsystem.setSpeed(.5);
            }
            else
            {
                Robot.climberSubsystem.setSpeed(0);

            }
            
        }
        else if (mode == MODE.CLIMB)
        {
            if (!Robot.climberSubsystem.isAtTop())
            {
                time = timeSinceInitialized();
                
                Robot.climberSubsystem.setSpeed(.5);
            }
            else if(mode == MODE.DOWN)
            {
                if(timeSinceInitialized() - time < Constants.CLIMBER_CLIMB_TIME_AFTER_TOP_REACHED)
                {
                    Robot.climberSubsystem.setSpeed(1);
                }
                else
                {
                    mode = MODE.OFF;
                }
            }
        }
        else if (mode == MODE.DOWN)
        {
            Robot.climberSubsystem.setSpeed(-.2);
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
            // no it wont we have a ratchet to keep that from happening
    }

    @Override
    protected void interrupted()
    {

    }
}
