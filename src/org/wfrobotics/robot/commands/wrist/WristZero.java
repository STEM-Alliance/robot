package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.Command;

public class WristZero extends Command
{
    private static boolean hasZeroed = false;
    private static double negate = -1;

    private final Wrist wrist = Wrist.getInstance();

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
        requires(wrist);
        setTimeout(2.0);
    }

    protected void execute()
    {
        boolean stalled = wrist.isStalled();
        if(stalled)
        {
            negate = -1 * negate;
        }
        wrist.setSpeed(negate * 0.3);  // Must be in execute in case interrupted
    }

    protected boolean isFinished()
    {
        boolean stalled = wrist.isStalled();

        wrist.AtBottom();
        if (stalled && negate == -1)
        {
            hasZeroed = true;
            wrist.setWristSensor(5482);
        }
        return hasZeroed;
    }

    protected void end()
    {
        wrist.setSpeed(0.0);
        hasZeroed = true;
    }

    protected void interrupted()
    {
        end();
    }
}
