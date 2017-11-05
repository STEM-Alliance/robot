package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

// TODO Should autodrive switch or optionally have polar?
// TODO Should we use brake for higher repeatability, does that make issues with drive subsystem PID's?
// TODO Constructor is broken, HerdVector takes polar not xy

/** Drive relative to the field. The robot's momentum is dampened when the command ends for greater repeatability. **/
public class AutoDrive extends Command
{
    protected RobotState state = RobotState.getInstance();
    protected final HerdVector robotRelative;

    public AutoDrive(double speedX, double speedY, double timeout)
    {
        requires(Robot.driveSubsystem);
        robotRelative = new HerdVector(speedX, speedY);
        setTimeout(timeout);
    }

    protected void execute()
    {
        HerdVector fieldRelative = robotRelative.rotate(-state.robotHeading);

        Robot.driveSubsystem.driveWithHeading(new SwerveSignal(fieldRelative));
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