package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.ExampleSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/** Sets ExampleSubsystem to a safe state */
public class ExampleStopCommand extends Command
{
    ExampleSubsystem exampleSubsystem = ExampleSubsystem.getInstance();
    public ExampleStopCommand()
    {
        requires(exampleSubsystem);
    }

    protected void execute()
    {
        double percentForward = 0.0;

        ExampleSubsystem.getInstance().setSpeed(percentForward);
    }

    protected boolean isFinished()
    {
        return true;
    }
}