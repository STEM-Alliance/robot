
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.drive.swerve.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class DriveSwerve extends Command 
{
    public enum MODE {HALO, COMBO, ANGLE, STOP}
    
    private final MODE mode;
    
    public DriveSwerve(MODE mode)
    {
        requires(Robot.driveSubsystem);
        
        this.mode = mode;
    }

    protected void initialize()
    {
        
    }

    protected void execute() 
    {
        Vector speedRobot;
        double speedRotation;

        Utilities.PrintCommand("Drive", this, mode.toString());

        switch(mode)
        {
            case HALO:
                Robot.driveSubsystem.setCrawlMode(OI.DriveSwerveOI.getCrawlSpeed());
    
                speedRobot = OI.DriveSwerveOI.getHaloDrive_Velocity();
                speedRotation = -OI.DriveSwerveOI.getHaloDrive_Rotation();
                break;
                
            case COMBO:
                double dpad = OI.DriveSwerveOI.getDpad();
                
                //((SwerveDriveSubsystem)Robot.driveSubsystem).setGearHigh(OI.DriveSwerveOI.getHighGearEnable());
                //((SwerveDriveSubsystem)Robot.driveSubsystem).setBrake(OI.DriveSwerveOI.getBrake());
                
                //if (OI.DriveSwerveOI.getResetGyro())
                //{
                //    ((SwerveDriveSubsystem)Robot.driveSubsystem).gyroZero();
                //}
                
                ((SwerveDriveSubsystem)Robot.driveSubsystem).setCrawlMode(OI.DriveSwerveOI.getCrawlSpeed());
                
                
                if (dpad != -1)
                {
                    Vector drive = new Vector();
                    drive.setMagAngle(1, (dpad - 90));
                    
                    // use non field relative
                    Robot.driveSubsystem.setFieldRelative(false);
                    
                    speedRobot = drive;
                    speedRotation = OI.DriveSwerveOI.getHaloDrive_Rotation();
                    //TODO fix this nonsense
                    ///OI.DriveSwerveOI.getHaloDrive_Heading45());
                }
                else
                {
                    //Robot.driveSubsystem.setFieldRelative(OI.DriveSwerveOI.getFieldRelative());
                    
                    speedRobot = OI.DriveSwerveOI.getHaloDrive_Velocity();
                    speedRotation = OI.DriveSwerveOI.getHaloDrive_Rotation();
                    //TODO fix this nonsense
                    ///OI.DriveSwerveOI.getHaloDrive_Heading45());
                }
                break;
                
            case ANGLE:
                //Robot.driveSubsystem.setFieldRelative(OI.DriveSwerveOI.getFieldRelative());
                
                speedRobot = OI.DriveSwerveOI.getAngleDrive_Velocity();
                speedRotation = 0;
                //TODO: handle the heading nonsense 
                // OI.DriveSwerveOI.getAngleDrive_Heading()
                break;
            
            case STOP:
            default:
                speedRobot = new Vector(0, 0);
                speedRotation = 0;
                Robot.driveSubsystem.setBrake(true);
                break;
        }
        
        Robot.driveSubsystem.driveVector(speedRobot, speedRotation);
    }

    protected boolean isFinished() 
    {
        return false;
    }

    protected void end() 
    {
        Robot.driveSubsystem.setBrake(false);
    }

    protected void interrupted() 
    {
        this.end();
    }
}
