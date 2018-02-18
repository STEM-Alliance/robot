package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftGoHome extends Command
{
    private static boolean hasZeroed = false;

    public static boolean everZeroed()
    {
        return hasZeroed;
    }

    public LiftGoHome()
    {
        requires(Robot.liftSubsystem);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Lift", getClass().getSimpleName());
    }

    protected void execute()
    {
        Robot.liftSubsystem.goToSpeedInit(getSpeed());
    }

    protected boolean isFinished()
    {
        boolean result = Robot.liftSubsystem.allSidesAtBottom();

        if (result)
        {
            hasZeroed = true;
        }
        return result;
    }

    private double getSpeed()
    {
        return -.2;
    }
}
