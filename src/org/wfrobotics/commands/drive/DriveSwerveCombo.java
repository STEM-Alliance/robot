
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.swerve.SwerveVector;

import edu.wpi.first.wpilibj.command.Command;

public class DriveSwerveCombo extends Command 
{
    public DriveSwerveCombo() 
    {
        requires(Robot.swerveDriveSubsystem);
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);
        
        double dpad = OI.DriveSwerveOI.getDpad();
        
        Robot.swerveDriveSubsystem.setGearHigh(OI.DriveSwerveOI.getHighGearEnable());
        Robot.swerveDriveSubsystem.setBrake(OI.DriveSwerveOI.getBrake());
        
        if (OI.DriveSwerveOI.getResetGyro())
        {
            Robot.swerveDriveSubsystem.ZeroGyro();
        }
        
        Robot.swerveDriveSubsystem.setCrawlMode(OI.DriveSwerveOI.getCrawlSpeed());
        
        
        if (dpad != -1)
        {
            SwerveVector drive = new SwerveVector();
            drive.setMagAngle(1, (dpad - 90));
            
            // use non field relative
            Robot.swerveDriveSubsystem.setFieldRelative(false);
            
            Robot.swerveDriveSubsystem.UpdateDrive(drive,
                    OI.DriveSwerveOI.getHaloDrive_Rotation(),
                    OI.DriveSwerveOI.getHaloDrive_Heading45());
        }
        else
        {
            Robot.swerveDriveSubsystem.setFieldRelative(OI.DriveSwerveOI.getFieldRelative());
            
            Robot.swerveDriveSubsystem.UpdateDrive(OI.DriveSwerveOI.getHaloDrive_Velocity(),
                    OI.DriveSwerveOI.getHaloDrive_Rotation(),
                    OI.DriveSwerveOI.getHaloDrive_Heading45());
        }
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
