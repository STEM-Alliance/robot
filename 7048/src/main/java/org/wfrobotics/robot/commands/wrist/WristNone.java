package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.subsystems.WristPneumatic;

import edu.wpi.first.wpilibj.command.Command;

public class WristNone extends Command
{
    private final WristPneumatic intake = WristPneumatic.getInstance();

    public WristNone()
    {
        requires(intake);
    }

    protected boolean isFinished()
    {
        return false;
    }
}

