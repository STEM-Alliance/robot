package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.prototype.config.IO;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.command.Command;

/** Sets ExampleSubsystem to a safe state */
public class LiftOpenLoop extends Command
{
    public LiftOpenLoop()
    {
        requires(Robot.prototypeSubsystem);
    }

    protected void execute()
    {
        double percentForward = IO.controller.getY(Hand.kRight);
        Robot.prototypeSubsystem.setOpenLoop(percentForward);
    }

    protected boolean isFinished()
    {
        return false;
    }
}