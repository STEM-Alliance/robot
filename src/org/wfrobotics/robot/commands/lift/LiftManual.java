package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.LiftHeight;
import org.wfrobotics.robot.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class LiftManual extends Command
{
    private final double kUp = LiftHeight.Scale.get();
    private final double kDown = LiftHeight.Intake.get();
    private final double deadbandPercent = .2;

    private final LiftSubsystem lift = LiftSubsystem.getInstance();

    public LiftManual()
    {
        requires(lift);
    }

    protected void execute()
    {
        double setpoint = Robot.controls.getLiftStick();

        if (Math.abs(setpoint) < deadbandPercent)
        {
            lift.goToSpeedInit(0);  // Engage brake
        }
        else
        {
            setpoint = (setpoint > 0) ? kUp : kDown;
            lift.goToHeightInit(setpoint);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
