package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DetectShooter extends Command 
{
    public enum MODE {GETDATA, OFF};

    boolean endEarly = false;
    private final MODE mode;

    public DetectShooter(MODE mode)
    {
        requires(Robot.targetShooterSubsystem);
        
        this.mode = mode;
    }

    
    protected void initialize()
    {
        endEarly = false;
        if (mode == MODE.GETDATA)
        {
            Robot.targetShooterSubsystem.enable();
        }
    }
    
    @Override
    protected void execute()
    {
        Utilities.PrintCommand("CameraShooter", this, mode.toString());
        
        if (mode == MODE.GETDATA)
        {
            Robot.targetShooterSubsystem.run();
        }
    }

    @Override
    protected boolean isFinished()
    {
        return endEarly;
    }

    @Override
    protected void end()
    {
        Robot.targetShooterSubsystem.disable();
    }
    
    @Override
    protected void interrupted()
    {
        end();
    }
    
    public void get()
    {
        // TODO make some getters for the command group to call for data
    }
    
    public double getDistanceFromCenter()
    {
        return Robot.targetShooterSubsystem.DistanceFromCenter;
    }
    
    public boolean getIsFound()
    {
        return Robot.targetShooterSubsystem.InView && Robot.targetShooterSubsystem.isEnabled();
    }
    
    public boolean getIsEnabled()
    {
        return Robot.targetShooterSubsystem.isEnabled();
    }

    public double getFullWidth()
    {
        return Robot.targetShooterSubsystem.FullWidth;
    }


    public void endEarly()
    {
        endEarly = true;
    }
}
