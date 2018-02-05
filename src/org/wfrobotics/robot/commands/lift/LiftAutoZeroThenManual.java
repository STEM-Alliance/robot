package org.wfrobotics.robot.commands.lift;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class LiftAutoZeroThenManual extends ConditionalCommand
{
    public LiftAutoZeroThenManual()
    {
        super(new LiftPercentVoltage(), new LiftGoHome());
    }

    protected boolean condition()
    {
        return LiftGoHome.everZeroed();
    }
}
