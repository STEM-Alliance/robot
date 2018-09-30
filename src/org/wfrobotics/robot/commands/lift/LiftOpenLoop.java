package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.LiftHeight;
import org.wfrobotics.robot.subsystems.Lift;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class LiftOpenLoop extends Command
{
    private final double deadbandPercent = 0.1;
    private final Lift lift = Lift.getInstance();

    public LiftOpenLoop()
    {
        requires(lift);
    }

    protected void execute()
    {
        if (!DriverStation.getInstance().isAutonomous())  // TODO ConditionalCommand cancels requirements
        {
            final double liftHeight = lift.getPosition();
            double setpoint = Robot.controls.getLiftStick();

            if (Math.abs(setpoint) < deadbandPercent)
            {
                setpoint = 0.0;
            }
            else if (liftHeight < LiftHeight.Intake.get() + 4.0 && setpoint < 0.0)
            {
                setpoint /= 4.0;
            }
            else if (liftHeight > LiftHeight.Scale.get() - 0.5 && setpoint > 0.0)
            {
                setpoint /= 3.0;
            }
            lift.setOpenLoop(setpoint);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
