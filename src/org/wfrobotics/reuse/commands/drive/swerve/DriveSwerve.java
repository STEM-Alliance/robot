
package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.subsystems.swerve.chassis.Config;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveSwerve extends Command 
{
    public enum MODE {HALO, FUSION, COMBO, ANGLE, STOP}
    
    private boolean DPAD_MOVEMENT_ENABLE = true;
    private final double DPAD_MOVEMENT_SPEED_HG = .45;
    private final double DPAD_MOVEMENT_SPEED_LG = .6;
    private boolean AUTO_SHIFT_ENABLE = false;
    private final double AUTO_SHIFT_TIME = 1;
    private final double AUTO_SHIFT_SPEED = .5;
    
    private final MODE mode;
    private double highVelocityStart;

    private double dpadPrev = -1;
    private boolean fieldRelativeLast = false;
    private boolean gyroLast = false;
    
    public DriveSwerve(MODE mode)
    {
        requires(Robot.driveSubsystem);
        
        this.mode = mode;
    }

    protected void initialize()
    {
        highVelocityStart = timeSinceInitialized();
    }

    protected void execute() 
    {
        double dpad = Robot.controls.swerveOI.getDpad();
        Vector speedRobot;
        double speedRotation;

        Utilities.PrintCommand("Drive", this, mode.toString());

        switch(mode)
        {
            case HALO:
                Config.crawlModeMagnitude = Robot.controls.swerveOI.getCrawlSpeed();
    
                speedRobot = Robot.controls.swerveOI.getHaloDrive_Velocity();
                speedRotation = -Robot.controls.swerveOI.getHaloDrive_Rotation();
                break;
            case FUSION:
                
                Config.crawlModeMagnitude = Robot.controls.swerveOI.getCrawlSpeed();
                
                if(Robot.shooterSubsystem.isRunning())
                {
                    Config.crawlModeMagnitude = 1;
                }
                
                speedRobot = Robot.controls.swerveOI.getHaloDrive_Velocity();
                speedRotation = -Robot.controls.swerveOI.getHaloDrive_Rotation() -Robot.controls.swerveOI.getFusionDrive_Rotation();// + startFusionPosition;
                
                SmartDashboard.putNumber("Dpad", dpad);

                if(DPAD_MOVEMENT_ENABLE)
                {
                    if (dpad != -1)
                    {
                        Config.crawlModeMagnitude = 0;
                        
                        double speed = Robot.driveSubsystem.configSwerve.gearHigh ? DPAD_MOVEMENT_SPEED_HG : DPAD_MOVEMENT_SPEED_LG;
                        
                        if(!Robot.shooterSubsystem.isRunning())
                        {
                            speedRobot.setMagAngle(speed, -(dpad-90));
                            speedRotation *= .5;
                        }
                        else
                        {
                            speedRobot.setMagAngle(speed*.75, -(dpad-90));
                            speedRotation *= 1;
                        }
                        
                        if(dpadPrev == -1)
                        {
                            //we need to save off the field relative
                            fieldRelativeLast = Robot.driveSubsystem.getFieldRelative();
                            gyroLast = Robot.driveSubsystem.configSwerve.gyroEnable;
                        }
                        
                        // disable field relative
                        Robot.driveSubsystem.setFieldRelative(false);
                        Robot.driveSubsystem.configSwerve.gyroEnable = false;
                    }
                    else
                    {
                        if(dpadPrev != -1)
                        {
                            // now we can restore field relative
                            Robot.driveSubsystem.setFieldRelative(fieldRelativeLast);
                            Robot.driveSubsystem.configSwerve.gyroEnable = gyroLast;
                        }
                    }
                    dpadPrev = dpad;
                }
                
                if (speedRobot.getMag() < AUTO_SHIFT_SPEED)  // Allow high gear to "kick in" after AUTO_SHIFT_SPEED seconds of high speed
                {
                    highVelocityStart = timeSinceInitialized();
                }
                break;
            case COMBO:
                
                //((SwerveDriveSubsystem)Robot.driveSubsystem).setGearHigh(OI.DriveSwerveOI.getHighGearEnable());
                //((SwerveDriveSubsystem)Robot.driveSubsystem).setBrake(OI.DriveSwerveOI.getBrake());
                
                //if (OI.DriveSwerveOI.getResetGyro())
                //{
                //    ((SwerveDriveSubsystem)Robot.driveSubsystem).gyroZero();
                //}
                
                Config.crawlModeMagnitude = Robot.controls.swerveOI.getCrawlSpeed();
                
                
                if (dpad != -1)
                {
                    Vector drive = new Vector();
                    drive.setMagAngle(1, (dpad - 90));
                    
                    // use non field relative
                    Robot.driveSubsystem.setFieldRelative(false);
                    
                    speedRobot = drive;
                    speedRotation = Robot.controls.swerveOI.getHaloDrive_Rotation();
                    //TODO fix this nonsense
                    ///OI.DriveSwerveOI.getHaloDrive_Heading45());
                }
                else
                {
                    //Robot.driveSubsystem.setFieldRelative(OI.DriveSwerveOI.getFieldRelative());
                    
                    speedRobot = Robot.controls.swerveOI.getHaloDrive_Velocity();
                    speedRotation = Robot.controls.swerveOI.getHaloDrive_Rotation();
                    //TODO fix this nonsense
                    ///OI.DriveSwerveOI.getHaloDrive_Heading45());
                }
                break;
                
            case ANGLE:
                //Robot.driveSubsystem.setFieldRelative(OI.DriveSwerveOI.getFieldRelative());
                
                speedRobot = Robot.controls.swerveOI.getAngleDrive_Velocity();
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
        
        if (AUTO_SHIFT_ENABLE)
        {
            Robot.driveSubsystem.configSwerve.gearHigh = timeSinceInitialized() - highVelocityStart > AUTO_SHIFT_TIME && speedRobot.getMag() > AUTO_SHIFT_SPEED;
        }
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(speedRobot.getMag(), speedRobot.getAngle()), speedRotation));
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
