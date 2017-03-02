package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class Lift extends Command 
{
    public final double TIMEOUT_NO_GEAR = 1;
    
    public double timeLastSensed;
    
    public Lift()
    {
        requires(Robot.lifterSubsystem);
    }
    
    @Override
    protected void initialize()
    {
        timeLastSensed = timeSinceInitialized() - TIMEOUT_NO_GEAR;  // Start in the down state
    }
    
    @Override
    protected void execute()
    {
        boolean direction = true;
        
        if (Robot.lifterSubsystem.hasGear())
        {
            timeLastSensed = timeSinceInitialized();
        }
        else if (timeSinceInitialized() - timeLastSensed> TIMEOUT_NO_GEAR)
        {
            direction = false;
        }
        
        Robot.lifterSubsystem.set(direction);
    }

    @Override
    protected boolean isFinished()
    {
        return false;
    }
}
