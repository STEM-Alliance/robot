package org.wfrobotics.robot.commands.link;

import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.command.Command;

public class LinkGoHome extends Command
{
    private final Link link = Link.getInstance();
    private final double kSpeed;

    public LinkGoHome()
    {
        this(-0.35);
    }

    public LinkGoHome(double speed)
    {
        requires(link);
        kSpeed = speed;
        setTimeout(4.0);
    }

    protected void execute()
    {
        link.setOpenLoop(kSpeed);
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
