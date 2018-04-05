package org.wfrobotics.robot.commands.wrist;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class WristAutoZeroThenPercentVoltage extends ConditionalCommand
{
    public WristAutoZeroThenPercentVoltage()
    {
        super(new SmartWrist(), new SmartWrist());
    }

    protected boolean condition()
    {
        return WristZero.everZeroed();
    }

    protected void end()
    {
        //        Robot.wrist.setPosition(.95);
    }
}
