package org.wfrobotics.commands.drive;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveConfig extends Command
{
    public enum MODE {HIGH_GEAR, FIELD_RELATIVE, GYRO_ZERO, GYRO_DISABLE}
    
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
                Robot.driveSubsystem.configSwerve.gearHigh = !Robot.driveSubsystem.configSwerve.gearHigh;
                break;
            case FIELD_RELATIVE:
                Robot.driveSubsystem.setFieldRelative(!Robot.driveSubsystem.getFieldRelative());
                break;
            case GYRO_ZERO:
                Robot.driveSubsystem.gyroZero();
                break;
            case GYRO_DISABLE:
                Robot.driveSubsystem.configSwerve.gyroEnable = !Robot.driveSubsystem.configSwerve.gyroEnable;
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
