package org.wfrobotics.robot.commands.wrist;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class WristAutoZeroThenPercentVoltage extends ConditionalCommand
{
    public WristAutoZeroThenPercentVoltage()
    {
        super(new SmartWrist(), new WristZero());
    }

    protected boolean condition()
    {
        return WristZero.everZeroed();
    }
}
