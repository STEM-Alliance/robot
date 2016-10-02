
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.swerve.SwerveVector;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveSwerveWheelCalibration extends Command 
{
    public DriveSwerveWheelCalibration() 
    {
        requires(Robot.swerveDriveSubsystem);
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);
        
        Robot.swerveDriveSubsystem.setFieldRelative(false);
    
        Robot.swerveDriveSubsystem.UpdateHaloDrive(SwerveVector.NewFromMagAngle(.2, 0), 0);
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
