package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

/** Drive while letting swerve simultaneously oversee turning to an angle **/
public class AutoDriveWithHeading extends DriveCommand
{
    final SwerveSignal neutral = new SwerveSignal(new HerdVector(0, 0));
    protected final SwerveSignal s;

    public AutoDriveWithHeading(double speedX, double speedY, double angle, double timeout)
    {
        requires(Robot.driveService.getSubsystem());
        s = new SwerveSignal(new SwerveSignal(new HerdVector(speedX, speedY), 0, angle));
        setTimeout(timeout);
    }

    protected void execute()
    {
        Robot.driveService.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end()
    {
        Robot.driveService.driveWithHeading(neutral);
    }
}