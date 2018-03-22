package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;

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

    protected void end()
    {
        Robot.wrist.setIntakeLiftPosition(.95);
    }
}
