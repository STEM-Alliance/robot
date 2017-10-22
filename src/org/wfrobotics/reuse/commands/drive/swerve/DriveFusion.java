
package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.subsystems.swerve.chassis.Config;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Drive;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveFusion extends Command
{
    private final double DPAD_MOVEMENT_SPEED_HG = .45;
    private final double DPAD_MOVEMENT_SPEED_LG = .6;
    HerdLogger log = new HerdLogger(DriveFusion.class);

    private double highVelocityStart;

    private double dpadPrev = -1;
    private boolean fieldRelativeLast = false;
    private boolean gyroLast = false;

    public DriveFusion()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        log.debug("Drive", "Fusion");
        highVelocityStart = timeSinceInitialized();
    }

    protected void execute()
    {
        double dpad = Robot.controls.swerveIO.getDpad();
        HerdVector speedRobot;
        double speedRotation;

        Config.crawlModeMagnitude = Robot.controls.swerveIO.getCrawlSpeed();

        if(Robot.shooterSubsystem.isRunning())
        {
            Config.crawlModeMagnitude = 1;
        }

        speedRobot = Robot.controls.swerveIO.getHaloDrive_Velocity();
        speedRotation = -Robot.controls.swerveIO.getHaloDrive_Rotation() -Robot.controls.swerveIO.getFusionDrive_Rotation();// + startFusionPosition;

        SmartDashboard.putNumber("Dpad", dpad);

        if (dpad != -1)
        {
            Config.crawlModeMagnitude = 0;

            double speed = Robot.driveSubsystem.configSwerve.gearHigh ? DPAD_MOVEMENT_SPEED_HG : DPAD_MOVEMENT_SPEED_LG;

            if(!Robot.shooterSubsystem.isRunning())
            {
                speedRobot = new HerdVector(speed, -(dpad-90));
                speedRotation *= .5;
            }
            else
            {
                speedRobot = new HerdVector(speed*.75, -(dpad-90));
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
        else if(dpadPrev != -1)
        {
            // now we can restore field relative
            Robot.driveSubsystem.setFieldRelative(fieldRelativeLast);
            Robot.driveSubsystem.configSwerve.gyroEnable = gyroLast;
        }
        dpadPrev = dpad;

        if (speedRobot.getMag() < Drive.AUTO_SHIFT_SPEED)  // Allow high gear to "kick in" after AUTO_SHIFT_SPEED seconds of high speed
        {
            highVelocityStart = timeSinceInitialized();
        }

        if (Drive.AUTO_SHIFT_ENABLE)
        {
            Robot.driveSubsystem.configSwerve.gearHigh = timeSinceInitialized() - highVelocityStart > Drive.AUTO_SHIFT_TIME && speedRobot.getMag() > Drive.AUTO_SHIFT_SPEED;
        }
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(speedRobot, speedRotation));
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), 0));
    }
}
