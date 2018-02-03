package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftManual extends Command
{
    private final double deadbandPercent = .2;

    public LiftManual()
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
        return false;
    }

    private double getSpeed()
    {
        double slow = .5;
        double speed = Robot.controls.getLiftManual() * slow;

        if (Math.abs(speed) < deadbandPercent)
        {
            speed = 0;
        }

        return speed;
    }
}
