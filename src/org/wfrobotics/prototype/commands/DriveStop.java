package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.reuse.utilities.HerdVector;

/** Sets ExampleSubsystem to a safe state */
public class DriveStop extends DriveCommand
{
    final static HerdVector neutral = new HerdVector(0, 0);

    public DriveStop()
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