package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftPercentVoltage extends Command
{
    private final double deadbandPercent = .2;

    public LiftPercentVoltage()
    {
        requires(Robot.liftSubsystem);
    }

    protected void initialize()
    {
        SmartDashboard.putString("Lift", getClass().getSimpleName());
    }

    protected void execute()
    {
        double setpoint = Robot.controls.getLiftPercent();

        if (Math.abs(setpoint) < deadbandPercent)
        {
            Robot.liftSubsystem.goToSpeedInit(0);
        }
        else
        {
            Robot.liftSubsystem.goToSpeedInit(setpoint);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
