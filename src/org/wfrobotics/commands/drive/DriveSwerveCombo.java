
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.swerve.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class DriveSwerveCombo extends Command 
{
    public DriveSwerveCombo() 
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);
        
        double dpad = OI.DriveSwerveOI.getDpad();
        
        ((SwerveDriveSubsystem)Robot.driveSubsystem).setGearHigh(OI.DriveSwerveOI.getHighGearEnable());
        ((SwerveDriveSubsystem)Robot.driveSubsystem).setBrake(OI.DriveSwerveOI.getBrake());
        
        if (OI.DriveSwerveOI.getResetGyro())
        {
            ((SwerveDriveSubsystem)Robot.driveSubsystem).gyroZero();
        }
        
        ((SwerveDriveSubsystem)Robot.driveSubsystem).setCrawlMode(OI.DriveSwerveOI.getCrawlSpeed());
        
        
        if (dpad != -1)
        {
            Vector drive = new Vector();
            drive.setMagAngle(1, (dpad - 90));
            
            // use non field relative
            Robot.driveSubsystem.setFieldRelative(false);
            
            Robot.driveSubsystem.driveVector(drive, OI.DriveSwerveOI.getHaloDrive_Rotation());
            //TODO fix this nonsense
            ///OI.DriveSwerveOI.getHaloDrive_Heading45());
        }
        else
        {
            Robot.driveSubsystem.setFieldRelative(OI.DriveSwerveOI.getFieldRelative());
            
            Robot.driveSubsystem.driveVector(OI.DriveSwerveOI.getHaloDrive_Velocity(), OI.DriveSwerveOI.getHaloDrive_Rotation());
            //TODO fix this nonsense
            ///OI.DriveSwerveOI.getHaloDrive_Heading45());
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
