package org.wfrobotics.robot.commands.ParellelLink;

import org.wfrobotics.robot.subsystems.ParellelLink;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class LinkZeroThenOpenLoop extends ConditionalCommand
{
    public LinkZeroThenOpenLoop()
    {
        super(new SmartLink(), new LinkZero());
    }

    protected boolean condition()
    {
        return ParellelLink.getInstance().hasZeroed();
    }
}
