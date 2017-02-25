package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.command.Command;

public class GearCamera extends Command 
{
    public enum MODE {GETDATA, OFF};
    
    private final MODE mode;
    private TargetData lastData;
    
    public GearCamera(MODE mode)
    {
        requires(Robot.aligningSubsystem);
        
        this.mode = mode;
    }
    
    protected void execute()
    {
        if (mode == MODE.GETDATA)
        {
            lastData = Robot.aligningSubsystem.getData();
        }
    }

    @Override
    protected boolean isFinished()
    {
        return false;
    }
    
    public TargetData getData()
    {
        // TODO SmartDashboard.put
        return lastData;
    }
}
