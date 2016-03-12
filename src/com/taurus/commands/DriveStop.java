package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveStop extends Command
{
    public DriveStop() 
    {
        requires(Robot.rockerDriveSubsystem);
    }

    protected void initialize() 
    {
        
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);
        Robot.rockerDriveSubsystem.driveRaw(0, 0);
    }

    protected boolean isFinished() 
    {
        return true;  // Always run this command because it will be default command of the subsystem.
    }

    protected void end()
    {
        
    }

    protected void interrupted() 
    {
        
    }
}
