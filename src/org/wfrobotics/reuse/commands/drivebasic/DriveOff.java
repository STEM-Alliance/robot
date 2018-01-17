package org.wfrobotics.reuse.commands.drivebasic;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.Robot;

/** Safety command for drivetrain. Toggle or cancel to quit **/
public class DriveOff extends DriveCommand
{
    final HerdVector neutral = new HerdVector(0, 0);

    public DriveOff()
    {
        requires(Robot.driveService.getSubsystem());
    }

    protected void execute()
    {
        Robot.driveService.driveBasic(neutral);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
