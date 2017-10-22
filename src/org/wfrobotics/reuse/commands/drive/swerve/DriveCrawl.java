package org.wfrobotics.reuse.commands.drive.swerve;

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

    private boolean priorFieldRelative;
    private boolean priorGyro;

    public DriveCrawl()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        log.debug("Drive", "Crawl");

        priorFieldRelative = Robot.driveSubsystem.getFieldRelative();
        priorGyro = Robot.driveSubsystem.configSwerve.gyroEnable;

        Robot.driveSubsystem.setFieldRelative(false);
        Robot.driveSubsystem.configSwerve.gyroEnable = false;
    }

    protected void execute()
    {
        double dpad = Robot.controls.swerveIO.getCrawlDirection();
        double speed = Robot.driveSubsystem.configSwerve.gearHigh ? Drive.DPAD_MOVEMENT_SPEED_HG : Drive.DPAD_MOVEMENT_SPEED_LG;
        HerdVector speedRobot = new HerdVector(speed, -(dpad-90));
        double speedRotation = Robot.controls.swerveIO.getRotation();

        if(Robot.shooterSubsystem.isRunning())
        {
            Config.crawlModeMagnitude = 1;
            speedRobot.scale(.75);
        }
        else
        {
            Config.crawlModeMagnitude = Robot.controls.swerveIO.getCrawlSpeed();
            speedRotation *= .5;
        }
        log.debug("Dpad", dpad);

        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(speedRobot, speedRotation));
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        Robot.driveSubsystem.setFieldRelative(priorFieldRelative);
        Robot.driveSubsystem.configSwerve.gyroEnable = priorGyro;

        Config.crawlModeMagnitude = 0;
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0), 0));
    }
}
