package org.wfrobotics.commands.drive;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveConfig extends Command
{
    public enum MODE {HIGH_GEAR, FIELD_RELATIVE, ZERO_GYRO}
    
    private final MODE mode;
    
    public DriveConfig(MODE mode)
    {
        requires(Robot.driveSubsystem);
        
        this.mode = mode;
    }
    
    protected void initialize()
    {
        switch(mode)
        {
            case HIGH_GEAR:
                Robot.driveSubsystem.setGearHigh(!Robot.driveSubsystem.getGearHigh());
                break;
            case FIELD_RELATIVE:
                Robot.driveSubsystem.setFieldRelative(!Robot.driveSubsystem.getFieldRelative());
                break;
            case ZERO_GYRO:
                Robot.driveSubsystem.gyroZero();
                break;
            default:
                break;
        }
    }
    
    @Override
    protected boolean isFinished()
    {
        return true;
    }

}
