package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

/** Turn until reaching the  heading **/
public class TurnToHeading extends DriveCommand
{
    final HerdVector neutral = new HerdVector(0, 0);
    final double heading;
    final double tol;
    HerdVector vector;

    public TurnToHeading(double headingFieldRelative, double tolerance)
    {
        requires(Robot.driveService.getSubsystem());
        heading = headingFieldRelative;
        tol = tolerance;
        vector = new HerdVector(1, heading);  // TODO magnitude should be configurable, add to constructor?
    }

    protected void execute()
    {
        Robot.driveService.turnBasic(vector);
    }

    protected boolean isFinished()
    {
        return Math.abs(heading - state.robotHeading) < tol;
    }

    protected void end()
    {
        Robot.driveService.turnBasic(neutral);
    }
}
