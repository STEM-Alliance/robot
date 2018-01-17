package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

// TODO Should autodrive switch or optionally have polar?
// TODO Should we use brake for higher repeatability, does that make issues with drive subsystem PID's?
// TODO Constructor is broken, HerdVector takes polar not xy

/** Drive relative to the field. The robot's momentum is dampened when the command ends for greater repeatability. **/
public class AutoDrive extends DriveCommand
{
    final HerdVector neutral = new HerdVector(0, 0);
    protected final HerdVector robotRelative;

    public AutoDrive(HerdVector vector, double timeout)
    {
        requires(Robot.driveService.getSubsystem());
        robotRelative = vector;
        setTimeout(timeout);
    }

    protected void execute()
    {
        HerdVector fieldRelative = robotRelative.rotate(-state.robotHeading);

        Robot.driveService.driveBasic(fieldRelative);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end()
    {
        Robot.driveService.driveBasic(neutral);
    }
}