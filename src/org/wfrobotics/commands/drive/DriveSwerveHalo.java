
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveSwerveHalo extends Command 
{
    public DriveSwerveHalo() 
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);

        Robot.driveSubsystem.setCrawlMode(OI.DriveSwerveOI.getCrawlSpeed());

        Robot.driveSubsystem.driveVector(OI.DriveSwerveOI.getHaloDrive_Velocity(), -OI.DriveSwerveOI.getHaloDrive_Rotation());
        
    }

    protected boolean isFinished() 
    {
        return false;
    }

    protected void end() 
    {
    }

    protected void interrupted() 
    {
    }
}
