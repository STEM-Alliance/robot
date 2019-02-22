package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class WristZero extends Command
{
    private final Wrist wrist = Wrist.getInstance();

    public WristZero()
    {
        requires(wrist);
        setTimeout(3.0);
    }

    protected void execute()
    {
        wrist.setOpenLoop(-0.3);
    }

    protected boolean isFinished()
    {
        return wrist.hasZeroed() || isTimedOut();
    }

    protected void end()
    {
        wrist.setOpenLoop(0.0);
    }
}
