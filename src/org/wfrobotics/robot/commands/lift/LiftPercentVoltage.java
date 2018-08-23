package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class LiftPercentVoltage extends Command
{
    private final double deadbandPercent = 0.2;

    private final LiftSubsystem lift = LiftSubsystem.getInstance();

    public LiftPercentVoltage()
    {
        requires(lift);
    }

    protected void execute()
    {
        final double setpoint = Robot.controls.getLiftStick();

        if (Math.abs(setpoint) < deadbandPercent)
        {
            lift.goToSpeedInit(0.0);
        }
        else
        {
            lift.goToSpeedInit(setpoint);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
