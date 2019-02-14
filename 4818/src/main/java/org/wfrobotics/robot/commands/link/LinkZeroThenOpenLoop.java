package org.wfrobotics.robot.commands.link;

import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class LinkZeroThenOpenLoop extends ConditionalCommand
{
    public LinkZeroThenOpenLoop()
    {
        super(new LinkOpenLoop(), new LinkZero());
    }

    protected boolean condition()
    {
        return Link.getInstance().hasZeroed();
    }
}
