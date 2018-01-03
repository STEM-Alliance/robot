package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AutoDrive extends Command
{
    double startTime;
    public AutoDrive()
    {
        requires(Robot.prototypeSubsystem);
    }
    
    protected void initialize()
    {
        startTime = timeSinceInitialized();
    }
    
    protected void execute()
    {
        if(timeSinceInitialized() - startTime <= 3)
        {
            Robot.prototypeSubsystem.setSpeed(0.25, 0.1);
        } else { 
            Robot.prototypeSubsystem.setSpeed(-0.25, 0.1); 
        }
    }

    
    protected boolean isFinished()
    {
        if(timeSinceInitialized() - startTime >= 6)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
