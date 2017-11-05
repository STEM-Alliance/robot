package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.config.Drive;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

//* Teleop fine control Herd swerve. Control is robot relative. **/
public class DriveCrawl extends Command
{
    RobotState state = RobotState.getInstance();
    HerdLogger log = new HerdLogger(DriveCrawl.class);

    double minSpeed;

    public DriveCrawl()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        log.info("Drive Mode", "Crawl");
        minSpeed = Preferences.getInstance().getDouble("DRIVE_SPEED_CRAWL", Drive.CRAWL_SPEED_MIN);
    }

    protected void execute()
    {
        double maxSpeed = (state.robotGear) ? Drive.CRAWL_SPEED_MAX_HG : Drive.CRAWL_SPEED_MAX_LG;
        HerdVector io = Robot.controls.swerveIO.getCrawl();
        HerdVector scaled = io.scaleToRange(minSpeed, maxSpeed);
        HerdVector robotRelative = scaled.rotate(state.robotHeading);

        log.debug("Drive IO", io);
        log.info("Robot Relative", robotRelative);
        log.info("Field Relative", scaled);
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(robotRelative));
    }

    protected boolean isFinished()
    {
        return false;
    }

    protected void end()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0)));
    }
}
