package org.wfrobotics.robot.path;

import org.wfrobotics.reuse.commands.drive.DriveCommand;
import org.wfrobotics.robot.Robot;

public class DriveInfared extends DriveCommand
{
    private final double target;
    private final double tol;

    public DriveInfared(double targetDistance, double absoluteTol)
    {
        requires(Robot.driveService);
        target = targetDistance;
        tol = absoluteTol;
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
        return Math.abs(remaining()) < tol;
    }

    private double remaining()
    {
        return state.intakeDistance / 2.54 - target;
    }
}
