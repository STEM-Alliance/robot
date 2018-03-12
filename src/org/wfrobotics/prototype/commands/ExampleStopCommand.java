package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.prototype.config.IO;
import org.wfrobotics.reuse.controller.Xbox.AXIS;

import edu.wpi.first.wpilibj.command.Command;

/** Sets ExampleSubsystem to a safe state */
public class ExampleStopCommand extends Command
{
    public ExampleStopCommand()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void execute()
    {
        double raw = IO.controller.getAxis(AXIS.RIGHT_Y);
        double speed = (Math.abs(raw) > .1) ? raw : 0;
        Robot.prototypeSubsystem.driveDifferential(speed);
    }

    protected boolean isFinished()
    {
        return false;
    }
}