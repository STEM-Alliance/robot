package com.taurus.commands;

import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveBrakeMode extends Command
{
    private boolean enable;
    
    public DriveBrakeMode(boolean enable) 
    {
        requires(Robot.rockerDriveSubsystem);
        this.enable = enable;
    }

    protected void initialize() 
    {
        Robot.rockerDriveSubsystem.setBrakeMode(enable);
    }

    protected void execute() 
    {
        
    }

    protected boolean isFinished() 
    {
        return true;
    }

    protected void end()
    {
        
    }

    protected void interrupted() 
    {
        
    }
}
