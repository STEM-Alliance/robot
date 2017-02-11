package org.wfrobotics.commands.drive;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveConfigToggle extends Command
{
    public enum MODE {HIGH_GEAR, FIELD_RELATIVE}
    
    private final MODE mode;
    
    public DriveConfigToggle(MODE mode)
    {
        requires(Robot.driveSubsystem);
        
        this.mode = mode;
    }
    
    protected void initialize()
    {
        if (mode == MODE.HIGH_GEAR)
        {
            Robot.driveSubsystem.setGearHigh(!Robot.driveSubsystem.getGearHigh());
        }
        else if (mode == MODE.FIELD_RELATIVE)
        {
            Robot.driveSubsystem.setFieldRelative(!Robot.driveSubsystem.getFieldRelative());
        }
    }
    
    @Override
    protected boolean isFinished()
    {
        return true;
    }

}
