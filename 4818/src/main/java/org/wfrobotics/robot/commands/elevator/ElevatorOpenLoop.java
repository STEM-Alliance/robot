package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.Command;

public final class ElevatorOpenLoop extends Command
{
    private final Elevator elevator = Elevator.getInstance();
    private final IO io = IO.getInstance();

    public ElevatorOpenLoop()
    {
        requires(elevator);
    }

    protected void execute()
    {
        final double speed = io.getElevatorStick();
        // elevator.getPosition();

        //            if (Math.abs(setpoint) < deadbandPercent)
        //            {
        //                setpoint = 0.0;
        //            }
        //            else if (height < FieldHeight.HatchLow.get() + 4.0 && setpoint < 0.0)
        //            {
        //                setpoint /= 4.0;
        //            }
        //            else if (height > FieldHeight.HatchHigh.get() - 0.5 && setpoint > 0.0)
        //            {
        //                setpoint /= 3.0;
        //            }

        // if (elevator.hasZeroed())
        // {
            //                if (height < )
            //                {
            //
            //                }
        // }

        elevator.setOpenLoop(speed);
    }

    protected boolean isFinished()
    {
        return false;
    }
}
