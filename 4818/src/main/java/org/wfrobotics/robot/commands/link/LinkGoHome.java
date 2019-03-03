package org.wfrobotics.robot.commands.link;

import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.command.Command;

public class LinkGoHome extends Command
{
    private final Link link = Link.getInstance();

    public LinkGoHome()
    {
        requires(link);
        setTimeout(5.0);
    }

    protected void execute()
    {
        link.setOpenLoop(-0.4);
    }

    protected boolean isFinished()
    {
        return link.hasZeroed() || isTimedOut();
    }

    protected void end()
    {
        link.setOpenLoop(0.0);
    }
}
