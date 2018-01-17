package org.wfrobotics.reuse.commands.differential;

import org.wfrobotics.reuse.commands.DriveCommand;
import org.wfrobotics.robot.Robot;

public class DriveTank extends DriveCommand
{
    public DriveTank()
    {
        requires(Robot.driveService.getSubsystem());
    }

    protected void execute()
    {
        // TODO drive with IO
    }

    protected boolean isFinished()
    {
        return false;
    }
}
