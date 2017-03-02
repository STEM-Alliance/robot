package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class Lift extends Command 
{
    public final double TIMEOUT_NO_GEAR = 1;
    public final double SAMPLES_UNTIL_LIFT = 5;
    
    public double timeLastSensed;
    public double samplesWithGear;
    
    public Lift()
    {
        requires(Robot.lifterSubsystem);
        samplesWithGear = 0;
    }
    
    @Override
    protected void initialize()
    {
        timeLastSensed = timeSinceInitialized() - TIMEOUT_NO_GEAR;  // Start in the down state
    }
    
    @Override
    protected void execute()
    {
        boolean direction;
        double now = timeSinceInitialized();
        
        if (Robot.lifterSubsystem.hasGear())
        {
            direction = (++samplesWithGear > SAMPLES_UNTIL_LIFT);  // How many cycles have we had a gear?
            timeLastSensed = now;
        }
        else
        {
            direction = now - timeLastSensed > TIMEOUT_NO_GEAR;  // How long since we had a gear?
        }
        
        Robot.lifterSubsystem.set(direction);
    }

    @Override
    protected boolean isFinished()
    {
        return false;
    }
}
