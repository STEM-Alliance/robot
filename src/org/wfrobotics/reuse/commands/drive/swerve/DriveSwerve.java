
package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.Drive;

import edu.wpi.first.wpilibj.command.Command;

public class DriveSwerve extends Command
{
    HerdLogger log = new HerdLogger(DriveSwerve.class);

    private double highVelocityStart;

    public DriveSwerve()
    {
        requires(Robot.driveSubsystem);
    }

    protected void initialize()
    {
        log.info("Drive Mode", "Swerve");
        highVelocityStart = timeSinceInitialized();
    }

    protected void execute()
    {
        HerdVector speedRobot = Robot.controls.swerveIO.getVelocity();
        double speedRotation = Robot.controls.swerveIO.getRotation();

        if (Drive.AUTO_SHIFT_ENABLE)
        {
            if (speedRobot.getMag() < Drive.AUTO_SHIFT_SPEED)  // Allow high gear to "kick in" after AUTO_SHIFT_SPEED seconds of high speed
            {
                highVelocityStart = timeSinceInitialized();
            }
            Robot.driveSubsystem.requestedHighGear = timeSinceInitialized() - highVelocityStart > Drive.AUTO_SHIFT_TIME && speedRobot.getMag() > Drive.AUTO_SHIFT_SPEED;
        }

        // TODO Move square mag to here?

        log.info("Drive Input", speedRobot);
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
