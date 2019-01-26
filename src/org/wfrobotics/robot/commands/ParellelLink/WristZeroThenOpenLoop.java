package org.wfrobotics.robot.commands.ParellelLink;

import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class WristZeroThenOpenLoop extends ConditionalCommand
{
    public WristZeroThenOpenLoop()
    {
        super(new SmartWrist(), new WristZero());
    }

    protected boolean condition()
    {
        return Wrist.getInstance().hasZeroed();
    }
}
