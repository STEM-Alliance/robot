package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.commands.DriveCommand;

public class DriveConstAccel extends DriveCommand
{
    public DriveConstAccel()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void initialize()
    {
        super.initialize();

        Robot.prototypeSubsystem.doMagic(1);
    }

    protected void execute()
    {
        Robot.prototypeSubsystem.update();
    }

    protected boolean isFinished()
    {
        return false;
    }
}
