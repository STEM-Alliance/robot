package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WristZero extends InstantCommand
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
    }

    protected void initialize()
    {
        SmartDashboard.putString("Wrist", this.getClass().getSimpleName());
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
        return hasZeroed || isTimedOut();
    }

    protected void end()
    {
        Robot.wrist.setPosition(.95);
    }
}
