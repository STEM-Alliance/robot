package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class LiftOpenLoop extends Command
{
    private final double deadbandPercent = 0.2;
    private final LiftSubsystem lift = LiftSubsystem.getInstance();

    public LiftOpenLoop()
    {
        requires(lift);
    }

    protected void execute()
    {
        final double setpoint = Robot.controls.getLiftStick();

        if (Math.abs(setpoint) < deadbandPercent)
        {
            lift.setOpenLoop(0.0);
        }
        else
        {
            lift.setOpenLoop(setpoint);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
