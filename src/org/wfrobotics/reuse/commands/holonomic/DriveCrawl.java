package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Drive;

import edu.wpi.first.wpilibj.Preferences;

//* Teleop fine control Herd swerve. Control is robot relative. **/
public class DriveCrawl extends DriveCommand
{
    final SwerveSignal neutral = new SwerveSignal(new HerdVector(0, 0));
    double initialHeading;
    double minSpeed;

    public DriveCrawl()
    {
        requires(Robot.driveService.getSubsystem());
    }

    protected void initialize()
    {
        super.initialize();
        minSpeed = Preferences.getInstance().getDouble("DRIVE_SPEED_CRAWL", Drive.CRAWL_SPEED_MIN);
        initialHeading = state.robotHeading;
    }

    protected void execute()
    {
        final double maxSpeed = (state.robotGear) ? Drive.CRAWL_SPEED_MAX_HG : Drive.CRAWL_SPEED_MAX_LG;
        final HerdVector io = Robot.controls.swerveIO.getCrawl();
        final HerdVector scaled = io.scaleToRange(minSpeed, maxSpeed);
        final HerdVector robotRelative = scaled.rotate(initialHeading);

        log.debug("Drive IO", io);
        log.info("Robot Relative", robotRelative);
        log.info("Field Relative", scaled);
        Robot.driveService.driveWithHeading(new SwerveSignal(robotRelative, 0, initialHeading));
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        Robot.driveService.driveWithHeading(neutral);
    }
}
