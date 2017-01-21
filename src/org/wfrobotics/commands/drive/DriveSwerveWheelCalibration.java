
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveSwerveWheelCalibration extends Command 
{
    public DriveSwerveWheelCalibration() 
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);
        
        Robot.driveSubsystem.setFieldRelative(false);
    
        Robot.driveSubsystem.driveVector(Vector.NewFromMagAngle(.75, 0), 0);
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
