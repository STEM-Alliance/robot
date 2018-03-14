package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class IntakeLiftAutoZeroThenPercentVoltage extends ConditionalCommand
{
    public IntakeLiftAutoZeroThenPercentVoltage()
    {
        super(new IntakeLift(), new IntakeLiftZero());
    }

    protected boolean condition()
    {
        return IntakeLiftZero.everZeroed();
    }

    protected void end()
    {
        Robot.wrist.setIntakeLiftPosition(.95);
    }
}
