package org.wfrobotics.reuse.commands.drive.swerve;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSignal;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

/** Turn until reaching the  heading **/
public class TurnToHeading extends Command
{
    RobotState state = RobotState.getInstance();

    SwerveSignal s;
    double heading;
    double tol;

    public TurnToHeading(double headingFieldRelative, double tolerance)
    {
        requires(Robot.driveSubsystem);
        heading = headingFieldRelative;
        tol = tolerance;
        s = new SwerveSignal(new HerdVector(0, 0), 1, heading);
    }

    protected void execute()
    {
        Robot.driveSubsystem.driveWithHeading(s);
    }

    protected boolean isFinished()
    {
        return Math.abs(heading - state.robotHeading) < tol;
    }
}
