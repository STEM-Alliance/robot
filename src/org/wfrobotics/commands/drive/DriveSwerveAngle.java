
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveSwerveAngle extends Command 
{
    public DriveSwerveAngle() 
    {
        requires(Robot.swerveDriveSubsystem);
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);

        Robot.swerveDriveSubsystem.setFieldRelative(OI.DriveSwerveOI.getFieldRelative());
    
        Robot.swerveDriveSubsystem.UpdateDrive(OI.DriveSwerveOI.getAngleDrive_Velocity(),
                0,
                OI.DriveSwerveOI.getAngleDrive_Heading());
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
