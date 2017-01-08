package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTankStop extends Command
{
    public DriveTankStop() 
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize() 
    {
        
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);
        Robot.driveSubsystem.driveTank(0, 0);
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
