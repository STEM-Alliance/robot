package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

/** Drive given distance based on wheel encoders */
public class AutoDriveDistance extends DriveCommand
{
    protected final double targetDistance;

    /** Drive given distance along linear path. Repeatable. */
    public AutoDriveDistance(double inchesForward)
    {
        targetDistance = inchesForward;
    }

    /** Drive given distance along linear path. Repeatable if timeout is generous. */
    public AutoDriveDistance(double inchesForward, double timeout)
    {
        this(inchesForward);
        setTimeout(timeout);
    }

    protected void initialize()
    {
        state.resetRobotDistanceDriven();
    }

    protected void execute()
    {
        // TODO Use repeatable drive strategy with constant acceleration for wheel slip, such as magic motion talon feature
        // TODO How should different close loop drive modes set switched?
    }

    protected boolean isFinished()
    {
        return state.robotDistanceDriven > targetDistance || isTimedOut();
    }

    protected void end()
    {
        Robot.driveService.driveBasic(new HerdVector(0, 0));
    }
}
