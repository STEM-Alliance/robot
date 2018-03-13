package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class IntakeLiftZero extends InstantCommand
{
    private static boolean hasZeroed = false;

    public static boolean everZeroed()
    {
        return hasZeroed;
    }

    public static void reset()
    {
        hasZeroed = false;
    }

    public IntakeLiftZero()
    {
        requires(Robot.wrist);
    }

    protected void initialize()
    {
        Robot.wrist.setIntakeLiftSpeed(-0.3);
    }

    protected boolean isFinished()
    {
        boolean result = Robot.wrist.intakeLiftAtBottom();

        if (result)
        {
            hasZeroed = true;
        }
        return hasZeroed || isTimedOut();
    }

    protected void end()
    {
        Robot.wrist.setIntakeLiftPosition(.95);
    }
}
