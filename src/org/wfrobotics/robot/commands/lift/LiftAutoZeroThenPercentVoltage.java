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
        boolean val = LiftGoHome.everZeroed();
        System.out.println(val);
        return val;
    }
}
