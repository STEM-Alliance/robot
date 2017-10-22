package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.subsystems.swerve.chassis.Config;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Drive;

import edu.wpi.first.wpilibj.command.Command;

public class DriveCombo extends Command
{
    HerdLogger log = new HerdLogger(DriveCombo.class);

    private double highVelocityStart;

    public DriveCombo()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        log.debug("Drive", "Combo");
        highVelocityStart = timeSinceInitialized();
    }

    protected void execute()
    {
        double dpad = Robot.controls.swerveIO.getDpad();
        HerdVector speedRobot;
        double speedRotation;

        //((SwerveDriveSubsystem)Robot.driveSubsystem).setGearHigh(OI.DriveSwerveOI.getHighGearEnable());

        //if (OI.DriveSwerveOI.getResetGyro())
        //{
        //    ((SwerveDriveSubsystem)Robot.driveSubsystem).gyroZero();
        //}

        Config.crawlModeMagnitude = Robot.controls.swerveIO.getCrawlSpeed();

        if (dpad != -1)
        {
            HerdVector drive = new HerdVector(1, (dpad - 90));

            // use non field relative
            Robot.driveSubsystem.setFieldRelative(false);

            speedRobot = drive;
            speedRotation = Robot.controls.swerveIO.getHaloDrive_Rotation();
            //TODO fix this nonsense
            ///OI.DriveSwerveOI.getHaloDrive_Heading45());
        }
        else
        {
            //Robot.driveSubsystem.setFieldRelative(OI.DriveSwerveOI.getFieldRelative());

            speedRobot = Robot.controls.swerveIO.getHaloDrive_Velocity();
            speedRotation = Robot.controls.swerveIO.getHaloDrive_Rotation();
            //TODO fix this nonsense
            ///OI.DriveSwerveOI.getHaloDrive_Heading45());
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
