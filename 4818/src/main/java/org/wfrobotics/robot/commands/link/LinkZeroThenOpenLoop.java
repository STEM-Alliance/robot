package org.wfrobotics.robot.commands.link;

import org.wfrobotics.robot.subsystems.Link;

import edu.wpi.first.wpilibj.command.CommandGroup;
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

    public static class LinkZero extends CommandGroup
    {
        public LinkZero()
        {
            addSequential(new LinkGoHome());
            addSequential(new LinkToHeight(10.0));
        }

        protected void end()
        {
            Link.getInstance().setOpenLoop(0.0);
        }
    }
}
