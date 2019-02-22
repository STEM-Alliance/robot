package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

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
        int percentForward = 0;

        Robot.prototypeSubsystem.setSpeed(percentForward);
    }

    protected boolean isFinished()
    {
        return true;
    }
}