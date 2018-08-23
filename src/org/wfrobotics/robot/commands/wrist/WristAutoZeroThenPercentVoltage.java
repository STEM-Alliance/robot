package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class WristAutoZeroThenPercentVoltage extends ConditionalCommand
{
    public WristAutoZeroThenPercentVoltage()
    {
        super(new SmartWrist(), new WristZero());
    }

    protected boolean condition()
    {
        return Wrist.getInstance().hasZeroed();
    }
}
