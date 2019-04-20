package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;

public class ElevatorOpenLoop extends Command
{
    private final double deadbandPercent = 0.1;
    private final Elevator lift = Elevator.getInstance();
    private final IO io = IO.getInstance();

    public ElevatorOpenLoop()
    {
        requires(lift);
    }

    protected void execute()
    {
        if (!DriverStation.getInstance().isAutonomous())  // TODO ConditionalCommand cancels requirements
        {
            lift.getPosition();
            double setpoint = io.getElevatorStick();

            if (Math.abs(setpoint) < deadbandPercent)
            {
                setpoint = 0.0;
            }
            //            else if (liftHeight < ArmHeight.BottomLimit.get() + 4.0 && setpoint < 0.0)
            //            {
            //                setpoint /= 4.0;
            //            }
            //            else if (liftHeight > ArmHeight.HatchHigh.get() - 0.5 && setpoint > 0.0)
            //            {
            //                setpoint /= 3.0;
            //            }
            lift.setOpenLoop(setpoint);
        }
    }

    protected boolean isFinished()
    {
        return false;
    }
}
