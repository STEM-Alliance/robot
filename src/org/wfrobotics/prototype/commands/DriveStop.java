package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;

/** Sets ExampleSubsystem to a safe state */
public class DriveStop extends Command
{
    public DriveStop()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void execute()
    {
        Robot.prototypeSubsystem.doMagic(0);
    }

    protected boolean isFinished()
    {
        return true;
    }
}