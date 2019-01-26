package org.wfrobotics.robot.commands.ParellelLink;

import org.wfrobotics.robot.subsystems.ParellelLink;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class WristZeroThenOpenLoop extends ConditionalCommand
{
    public WristZeroThenOpenLoop()
    {
        super(new SmartLink(), new LinkZero());
    }

    protected boolean condition()
    {
        return ParellelLink.getInstance().hasZeroed();
    }
}
