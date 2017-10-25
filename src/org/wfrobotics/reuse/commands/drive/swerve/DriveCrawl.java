package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.subsystems.swerve.chassis.Config;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Drive;

import edu.wpi.first.wpilibj.command.Command;

public class DriveCrawl extends Command
{
    HerdLogger log = new HerdLogger(DriveCrawl.class);

    public DriveCrawl()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        log.debug("Drive", "Crawl");
    }

    protected void execute()
    {
        double dpadSpeed = Robot.driveSubsystem.requestHighGear ? Drive.DPAD_MOVEMENT_SPEED_HG : Drive.DPAD_MOVEMENT_SPEED_LG;
        double dpad = Robot.controls.swerveIO.getCrawlDirection();
        HerdVector speedRobot = new HerdVector(dpadSpeed, -(dpad-90));
        HerdVector fieldRelative;

        log.debug("Dpad", dpad);
        Config.crawlModeMagnitude = Robot.controls.swerveIO.getCrawlSpeed();
        fieldRelative = speedRobot.rotate(Gyro.getInstance().getYaw());

        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(fieldRelative, 0));
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        Config.crawlModeMagnitude = 0;
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), 0));
    }
}
