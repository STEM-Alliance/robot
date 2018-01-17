package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class Strafe extends Command
{
    final SwerveSignal s;

    public Strafe(double xSpeed, double timeout)
    {
        requires(Robot.driveSubsystem);
        s = new SwerveSignal(new HerdVector(xSpeed, 0));
        setTimeout(timeout);
    }

    protected void execute()
    {
        Robot.driveSubsystem.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end()
    {
        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(new HerdVector(0, 0)));
    }
}
