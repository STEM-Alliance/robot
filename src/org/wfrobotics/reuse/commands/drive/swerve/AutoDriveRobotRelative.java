package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.robot.Robot;

/** Drive relative to the robot's orientation **/
public class AutoDriveRobotRelative extends AutoDrive
{
    public AutoDriveRobotRelative(double speedX, double speedY, double timeout)
    {
        super(speedX, speedY, timeout);
    }

    protected void execute()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(robotRelative));
    }
}
