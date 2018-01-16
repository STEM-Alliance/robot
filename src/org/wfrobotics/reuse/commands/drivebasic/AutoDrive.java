package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.subsystems.drive.DriveService;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;

// TODO Should autodrive switch or optionally have polar?
// TODO Should we use brake for higher repeatability, does that make issues with drive subsystem PID's?
// TODO Constructor is broken, HerdVector takes polar not xy

/** Drive relative to the field. The robot's momentum is dampened when the command ends for greater repeatability. **/
public class AutoDrive extends Command
{
    protected RobotState state = RobotState.getInstance();
    protected DriveService<?> driveHelper;
    protected final HerdVector robotRelative;

    public AutoDrive(DriveService<?> helper, HerdVector vector, double timeout)
    {
        driveHelper = helper;
        requires(driveHelper.getDrive());
        robotRelative = vector;
        setTimeout(timeout);
    }

    protected void execute()
    {
        HerdVector fieldRelative = robotRelative.rotate(-state.robotHeading);

        driveHelper.getDrive().driveBasic(fieldRelative);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end()
    {
        driveHelper.getDrive().driveBasic(new HerdVector(0, 0));
    }
}