package org.wfrobotics.robot.commands.ParellelLink;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class WristZeroThenOpenLoop extends ConditionalCommand
{
    public WristZeroThenOpenLoop()
    {
        super(new SmartLink(), new LinkZero());
    }

    protected boolean condition()
    {
        return false;
        //        return ParellelLink.getInstance().hasZeroed();
    }
}
