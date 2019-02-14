package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class WristNone extends Command
{
    public WristNone()
    {
        requires(Wrist.getInstance());
    }

    protected boolean isFinished()
    {
        return false;
    }
}
