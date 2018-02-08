package org.wfrobotics.robot.commands.lift;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class LiftAutoZeroThenCal extends ConditionalCommand
{
    public LiftAutoZeroThenCal()
    {
        super(new LiftPercentVoltage(), new LiftGoHome());
    }

    protected boolean condition()
    {
        return LiftGoHome.everZeroed();
    }
}
