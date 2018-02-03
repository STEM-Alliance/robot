package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftToHeight extends Command
{
    private final double desired;

    public LiftToHeight(double desired)
    {
        requires(Robot.liftSubsystem);
        this.desired = desired;
    }

    protected void initialize()
    {
        SmartDashboard.putString("Lift", getClass().getSimpleName());
        Robot.liftSubsystem.goToHeightInit(desired);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
