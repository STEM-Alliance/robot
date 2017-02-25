package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Set robot LEDs
 * This command sets the highly visible LEDs mounted on the robot
 * Useful for communication of events to driver or human player, or flaunting after we do something awesome
 */
public class GearDetection extends Command
{
    public enum MODE {GETDATA, OFF};
    
    private final MODE mode;
    
    private double lastDistanceFromCenter;
    
    public GearDetection(MODE mode)
    {
        requires(Robot.targetGearSubsystem);
        
        this.mode = mode;
    }
    
    @Override
    protected void initialize()
    {
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
}
