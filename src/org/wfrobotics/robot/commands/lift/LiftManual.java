package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftManual extends Command
{
    private final double kUp = LiftHeight.Scale.get();
    private final double kDown = LiftHeight.Intake.get();
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
        double setpoint = Robot.controls.getLiftManual();

        if (Math.abs(setpoint) < deadbandPercent)
        {
            Robot.liftSubsystem.goToSpeedInit(0);
        }
        else
        {
            setpoint = (setpoint > 0) ? kUp : kDown;
            Robot.liftSubsystem.goToHeightInit(setpoint);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
