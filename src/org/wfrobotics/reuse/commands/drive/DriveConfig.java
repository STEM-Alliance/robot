package org.wfrobotics.reuse.commands.drive;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveConfig extends Command
{
    public enum MODE {HIGH_GEAR, FIELD_RELATIVE, GYRO_ZERO, GYRO_DISABLE}
    
    private final MODE mode;

    private boolean manual = false;
    private boolean enable = false;
    
    public DriveConfig(MODE mode)
    {
        requires(Robot.driveSubsystem);
        
        this.mode = mode;
        this.manual = false;
    }
    
    public DriveConfig(MODE mode, boolean enable)
    {
        requires(Robot.driveSubsystem);
        
        this.mode = mode;
        this.manual = true;
        this.enable = enable;
    }
    
    protected void initialize()
    {
        switch(mode)
        {
            case HIGH_GEAR:
                Robot.driveSubsystem.configSwerve.gearHigh = manual ? enable : !Robot.driveSubsystem.configSwerve.gearHigh;
                break;
            case FIELD_RELATIVE:
                Robot.driveSubsystem.setFieldRelative(manual ? enable : !Robot.driveSubsystem.getFieldRelative());
                break;
            case GYRO_ZERO:
                Robot.driveSubsystem.zeroGyro();
                break;
            case GYRO_DISABLE:
                Robot.driveSubsystem.configSwerve.gyroEnable = manual ? enable : !Robot.driveSubsystem.configSwerve.gyroEnable;
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
