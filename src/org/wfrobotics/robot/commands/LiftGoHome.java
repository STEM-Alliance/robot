package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftGoHome extends Command
{
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
        return Robot.liftSubsystem.isAtBottom();
    }

    private double getSpeed()
    {
        return -.2;
    }
}
