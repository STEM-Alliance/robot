package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class WristZero extends InstantCommand
{
    private static boolean hasZeroed = false;

    private final boolean kZeroAtBottom;

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
        kZeroAtBottom = Robot.config.WRIST_ZERO_BOTTOM;
    }

    protected void initialize()
    {
        SmartDashboard.putString("Wrist", this.getClass().getSimpleName());
    }

    protected void execute()
    {
        final double speed = (kZeroAtBottom) ? -0.3 : 0.4;
        Robot.wrist.setIntakeLiftSpeed(speed);  // Must be in execute in case interrupted
    }

    protected boolean isFinished()
    {
        boolean result = (kZeroAtBottom) ? Robot.wrist.intakeLiftAtBottom() : Robot.wrist.intakeLiftAtTop();

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
