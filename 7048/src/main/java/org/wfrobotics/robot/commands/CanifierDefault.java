package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.CanifierTest;

import edu.wpi.first.wpilibj.command.Command;

public class CanifierDefault extends Command
{
    private final CanifierTest subsystem = CanifierTest.getInstance();

    public CanifierDefault()
    {
        requires(subsystem);
    }

    protected void initialize()
    {
        // subsystem.canifier.off();
    }

    protected void execute()
    {
        // subsystem.canifier.setForTeleop();
    }

    protected boolean isFinished()
    {
        return false;
    }
}