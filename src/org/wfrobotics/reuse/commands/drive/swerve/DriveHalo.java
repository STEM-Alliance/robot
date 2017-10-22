package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.subsystems.swerve.chassis.Config;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Drive;

import edu.wpi.first.wpilibj.command.Command;

public class DriveHalo extends Command
{
    HerdLogger log = new HerdLogger(DriveHalo.class);

    private double highVelocityStart;

    public DriveHalo()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        log.debug("Drive", "Halo");
        highVelocityStart = timeSinceInitialized();
    }

    protected void execute()
    {
        HerdVector speedRobot = Robot.controls.swerveIO.getHaloDrive_Velocity();
        double speedRotation = -Robot.controls.swerveIO.getHaloDrive_Rotation();

        Config.crawlModeMagnitude = Robot.controls.swerveIO.getCrawlSpeed();

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
