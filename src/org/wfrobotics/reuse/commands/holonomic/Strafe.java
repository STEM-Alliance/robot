package org.wfrobotics.reuse.commands.holonomic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

public class Strafe extends DriveCommand
{
    final SwerveSignal neutral = new SwerveSignal(new HerdVector(0, 0));
    final SwerveSignal s;

    public Strafe(double xSpeed, double timeout)
    {
        requires(Robot.driveService.getSubsystem());
        s = new SwerveSignal(new HerdVector(xSpeed, 0));
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
