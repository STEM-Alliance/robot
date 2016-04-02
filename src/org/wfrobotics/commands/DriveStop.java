package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveStop extends Command
{
    public DriveStop() 
    {
        requires(Robot.tankDriveSubsystem);
    }

    protected void initialize() 
    {
        
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);
        Robot.tankDriveSubsystem.driveRaw(0, 0);
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
