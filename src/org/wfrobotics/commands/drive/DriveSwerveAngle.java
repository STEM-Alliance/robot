
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveSwerveAngle extends Command 
{
    public DriveSwerveAngle() 
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);

        Robot.driveSubsystem.setFieldRelative(OI.DriveSwerveOI.getFieldRelative());
    
        Robot.driveSubsystem.driveVector(OI.DriveSwerveOI.getAngleDrive_Velocity(), 0);
        //TODO: handle the heading nonsense 
        // OI.DriveSwerveOI.getAngleDrive_Heading()
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
