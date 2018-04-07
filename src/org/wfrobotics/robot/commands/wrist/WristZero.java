package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class WristZero extends Command
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

    public WristZero()
    {
        requires(Robot.wrist);
        setTimeout(2.0);
    }

    protected void execute()
    {
        Robot.wrist.setSpeed(-0.3);  // Must be in execute in case interrupted
    }

    protected boolean isFinished()
    {
        boolean result = Robot.wrist.AtBottom();

        if (result)
        {
            hasZeroed = true;
        }
        return hasZeroed;
    }

    protected void end()
    {
        //        Robot.wrist.setPosition(.95);
        Robot.wrist.setSpeed(0.0);
        hasZeroed = true;
    }

    protected void interrupted()
    {
        end();
    }
}
