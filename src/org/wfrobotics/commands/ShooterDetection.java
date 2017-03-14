package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ShooterDetection extends Command 
{
    public enum MODE {GETDATA, OFF};

    private final MODE mode;

    public ShooterDetection(MODE mode)
    {
        requires(Robot.targetingSubsystem);
        
        this.mode = mode;
    }
    
    @Override
    protected void execute()
    {
        Utilities.PrintCommand("CameraShooter", this, mode.toString());
        
        if (mode == MODE.GETDATA)
        {            
            Robot.targetingSubsystem.run();
        }
    }

    @Override
    protected boolean isFinished()
    {
        return false;
    }
    
    public void get()
    {
        // TODO make some getters for the command group to call for data
    }
    public double getDistanceFromCenter()
    {
        return Robot.targetingSubsystem.DistanceFromCenter;
    }
    
    public boolean getIsFound()
    {
        return Robot.targetingSubsystem.InView;
    }

    public double getFullWidth()
    {
        return Robot.targetingSubsystem.FullWidth;
    }

}
