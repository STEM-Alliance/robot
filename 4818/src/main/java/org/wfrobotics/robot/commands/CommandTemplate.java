package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.config.IO;

import edu.wpi.first.wpilibj.command.Command;

public class CommandTemplate extends Command
{
    // private final 'Subsystem' subsystem = Subsystem.getInstance();
    private final IO io = IO.getInstance();

    public CommandTemplate()
    {
        // requires(subsystem);
    }
    protected void execute()
    {

    }
    protected void end()
    {

    }
    protected boolean isFinished()
    {
        return false;
    }
}
