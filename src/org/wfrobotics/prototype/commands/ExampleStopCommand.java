package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

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
        int rpm = 0;

        Robot.prototypeSubsystem.setSpeed(rpm);
    }

    protected boolean isFinished()
    {
        return true;
    }
}