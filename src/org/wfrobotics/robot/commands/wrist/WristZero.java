package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class WristZero extends Command
{
    private static boolean hasZeroed = false;
    private static double negate = -1;

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
        boolean stalled = Robot.wrist.isStalled();
        if(stalled)
        {
            negate = -1 * negate;
        }
        Robot.wrist.setSpeed(negate * 0.3);  // Must be in execute in case interrupted
    }

    protected boolean isFinished()
    {
        boolean stalled = Robot.wrist.isStalled();

        Robot.wrist.AtBottom();
        if (stalled && negate == -1)
        {
            hasZeroed = true;
            Robot.wrist.setWristSensor(5482);
        }
        return hasZeroed;
    }

    protected void end()
    {
        Robot.wrist.setSpeed(0.0);
        hasZeroed = true;
    }

    protected void interrupted()
    {
        end();
    }
}
