package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.LiftHeight;
import org.wfrobotics.robot.subsystems.Lift;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class LiftOpenLoop extends Command
{
    private final double deadbandPercent = 0.1;
    private final Lift lift = Lift.getInstance();
    private final IO io = IO.getInstance();

    public LiftOpenLoop()
    {
        requires(lift);
    }

    protected void execute()
    {
        if (!DriverStation.getInstance().isAutonomous())  // TODO ConditionalCommand cancels requirements
        {
            final double liftHeight = lift.getPosition();
            double setpoint = io.getLiftStick();

            if (Math.abs(setpoint) < deadbandPercent)
            {
                setpoint = 0.0;
            }
            else if (liftHeight < LiftHeight.HatchLow.get() + 4.0 && setpoint < 0.0)
            {
                setpoint /= 4.0;
            }
            else if (liftHeight > LiftHeight.HatchHigh.get() - 0.5 && setpoint > 0.0)
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
