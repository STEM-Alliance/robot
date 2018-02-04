package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.reuse.controller.Xbox;
import org.wfrobotics.reuse.controller.Xbox.AXIS;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftToHeight extends Command
{
    private final double desired;
    private final Xbox controller;
    private final AXIS axis;
    private final double multiplier;

    public LiftToHeight(double desired)
    {
        requires(Robot.liftSubsystem);
        this.desired = desired;
        controller = null;
        axis = null;
        multiplier = 0;
    }

    public LiftToHeight(Xbox driver, AXIS axis, double multiplier)
    {
        requires(Robot.liftSubsystem);
        desired = 0;
        controller = driver;
        this.axis = axis;
        this.multiplier = multiplier;
    }

    protected void initialize()
    {
        SmartDashboard.putString("Lift", getClass().getSimpleName());

        if(desired != 0)
        {
            Robot.liftSubsystem.goToHeightInit(desired);
        }
        else
        {
            Robot.liftSubsystem.goToHeightInit(controller.getAxis(axis) * multiplier);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
