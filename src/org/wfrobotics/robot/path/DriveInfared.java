package org.wfrobotics.robot.path;

import org.wfrobotics.reuse.commands.drive.DriveCommand;
import org.wfrobotics.robot.Robot;

public class DriveInfared extends DriveCommand
{
    private final double target;
    private final double tol;
    private final double max;
    public DriveInfared(double targetDistance, double maxDistance, double absoluteTol, double timeout)
    {
        requires(Robot.driveService);
        target = targetDistance;
        tol = absoluteTol;
        max = maxDistance;
        setTimeout(timeout);
    }

    protected void initialize()
    {
        super.initialize();
        Robot.driveService.driveDistance(remaining());
    }

    protected void execute()
    {
        Robot.driveService.driveDistance(remaining());
    }

    protected boolean isFinished()
    {
        return Math.abs(remaining()) < tol || isTimedOut();
    }

    private double remaining()
    {
        return Math.min(max, state.intakeDistance / 2.54 - target);
    }
}
