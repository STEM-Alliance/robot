package org.wfrobotics.robot.path;

import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

public class DriveInfared extends Command
{
    private final RobotState state = RobotState.getInstance();
    private final TankSubsystem drive = TankSubsystem.getInstance();

    private final double target;
    private final double tol;

    public DriveInfared(double targetDistance, double absoluteTol, double timeout)
    {
        requires(drive);
        target = targetDistance;
        tol = absoluteTol;
        setTimeout(timeout);
    }

    protected void initialize()
    {
        drive.driveDistance(remaining());
    }

    protected void execute()
    {
        drive.driveDistance(remaining());
    }

    protected boolean isFinished()
    {
        return Math.abs(remaining()) < tol || isTimedOut();
    }

    private double remaining()
    {
        return state.intakeDistance / 2.54 - target;
    }
}
