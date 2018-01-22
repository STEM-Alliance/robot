package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.reuse.commands.DriveCommand;

public class ToggleConstAccel extends DriveCommand
{
    static boolean toggleDirection = true;

    public ToggleConstAccel()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void initialize()
    {
        super.initialize();
        double sign = toggleDirection ? 1 : -1;

        Robot.prototypeSubsystem.doMagic(1 * sign);

        toggleDirection = !toggleDirection;
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