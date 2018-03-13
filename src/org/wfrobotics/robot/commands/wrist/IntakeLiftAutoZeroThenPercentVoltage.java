package org.wfrobotics.robot.commands.wrist;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class IntakeLiftAutoZeroThenPercentVoltage extends ConditionalCommand
{
    public IntakeLiftAutoZeroThenPercentVoltage()
    {
        super(new IntakeLift(), new IntakeLiftZero());
    }

    protected boolean condition()
    {
        boolean val = IntakeLiftZero.everZeroed();
        System.out.println(val);
        return val;
    }
}
