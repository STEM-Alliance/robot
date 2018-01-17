package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/** Drive while letting swerve simultaneously oversee turning to an angle **/
public class AutoDriveWithHeading extends Command
{
    protected final SwerveSignal s;

    public AutoDriveWithHeading(double speedX, double speedY, double angle, double timeout)
    {
        requires(Robot.driveSubsystem);
        s = new SwerveSignal(new SwerveSignal(new HerdVector(speedX, speedY), 0, angle));
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