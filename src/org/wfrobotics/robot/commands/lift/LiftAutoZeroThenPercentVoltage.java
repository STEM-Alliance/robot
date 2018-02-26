package org.wfrobotics.robot.commands.lift;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class LiftAutoZeroThenPercentVoltage extends ConditionalCommand
{
    public LiftAutoZeroThenPercentVoltage()
    {
        super(new LiftPercentVoltage(), new AutoZero());
    }

    protected boolean condition()
    {
        return LiftGoHome.everZeroed();
    }
}
