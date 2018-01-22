package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.commands.DriveCommand;

/** Sets ExampleSubsystem to a safe state */
public class DriveStop extends DriveCommand
{
    public DriveStop()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void execute()
    {
        Robot.prototypeSubsystem.doDrive(0);
    }

    protected boolean isFinished()
    {
        return false;
    }
}