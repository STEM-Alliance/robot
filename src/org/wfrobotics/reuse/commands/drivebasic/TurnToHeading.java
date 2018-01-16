package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

/** Turn until reaching the  heading **/
public class TurnToHeading extends Command
{
    protected RobotState state = RobotState.getInstance();
    protected DriveService<?> driveHelper;

    HerdVector vector;
    double heading;
    double tol;

    public TurnToHeading(DriveService<?> helper, double headingFieldRelative, double tolerance)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
        heading = headingFieldRelative;
        tol = tolerance;
        vector = new HerdVector(1, heading);  // TODO magnitude should be configurable, add to constructor?
    }

    protected void execute()
    {
        driveHelper.getDrive().turnBasic(vector);
    }

    protected boolean isFinished()
    {
        return Math.abs(heading - state.robotHeading) < tol;
    }

    protected void end()
    {
        driveHelper.getDrive().turnBasic(new HerdVector(0, 0));
    }
}
