package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftGoHome extends Command
{
    private static boolean hasZeroed = false;

    private final LiftSubsystem lift = LiftSubsystem.getInstance();

    private final double speed;

    public static boolean everZeroed()
    {
        return hasZeroed;
    }

    public static void reset()
    {
        hasZeroed = false;
    }

    public LiftGoHome(double speed, double timeout)
    {
        requires(lift);
        this.speed = speed;
        setTimeout(timeout);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Lift", getClass().getSimpleName());
    }

    protected void execute()
    {
        lift.goToSpeedInit(speed);  // Must be in execute in case interrupted
    }

    protected boolean isFinished()
    {
        boolean result = lift.allSidesAtBottom();

        if (result)
        {
            hasZeroed = true;
        }
        return hasZeroed || isTimedOut() || lift.eitherSideAtTop();
    }

    protected void end()
    {
        lift.goToSpeedInit(0);
    }
}
