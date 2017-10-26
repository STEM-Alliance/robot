package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.subsystems.swerve.chassis.Config;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;
import org.wfrobotics.robot.config.Drive;

import edu.wpi.first.wpilibj.command.Command;

public class DriveCrawl extends Command
{
    RobotState state = RobotState.getInstance();
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
        HerdVector dpad = Robot.controls.swerveIO.getCrawl();
        HerdVector speedRobot = new HerdVector(dpadSpeed, dpad.getAngle());
        HerdVector fieldRelative = speedRobot.rotate(state.robotHeading);

        log.debug("Dpad", dpad);
        Config.crawlModeMagnitude = dpad.getMag();

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
