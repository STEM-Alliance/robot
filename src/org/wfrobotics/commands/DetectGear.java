package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Set robot LEDs
 * This command sets the highly visible LEDs mounted on the robot
 * Useful for communication of events to driver or human player, or flaunting after we do something awesome
 */
public class DetectGear extends Command
{
    public enum MODE {GETDATA, OFF};
    
    private final MODE mode;
    
    public DetectGear(MODE mode)
    {
        requires(Robot.targetGearSubsystem);
        
        Robot.targetGearSubsystem.enable();
        
        this.mode = mode;
    }
    
    @Override
    protected void initialize()
    {
        if (mode == MODE.GETDATA)
        {
            Robot.targetGearSubsystem.enable();
        }
    }

    @Override
    protected void execute()
    {
        Utilities.PrintCommand("CameraGear", this, mode.toString());
    
        if (mode == MODE.GETDATA)
        {
            Robot.targetGearSubsystem.run();
        }
    }
    
    @Override
    protected boolean isFinished()
    {
        return false;
    }

    @Override
    protected void end()
    {
        Robot.targetGearSubsystem.disable();
    }
    
    @Override
    protected void interrupted()
    {
        end();
    }
    
    public double getDistanceFromCenter()
    {
        return Robot.targetGearSubsystem.DistanceFromCenter;
    }
    
    public boolean getIsFound()
    {
        return Robot.targetGearSubsystem.InView;
    }

    public double getFullWidth()
    {
        return Robot.targetGearSubsystem.FullWidth;
    }

    public double getTargetDesiredWidth()
    {
        if(Robot.targetGearSubsystem.TargetCount == Robot.targetGearSubsystem.DESIRED_TARGETS)
        {
            return 350;
        }
        else if(Robot.targetGearSubsystem.TargetCount == 1)
        {
            return 70;
        }
        else
        {
            return 0;
        }
    }
}
