package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.Command;

public class ElevatorGoHome extends Command
{
    private final Elevator elevator = Elevator.getInstance();
    private final IO io = IO.getInstance();
    private final double speed;

    public ElevatorGoHome(double speed, double timeout)
    {
        requires(elevator);
        this.speed = speed;
        setTimeout(timeout);
    }

    protected void initialize()
    {
        elevator.setOpenLoop(speed);
    }

    protected boolean isFinished()
    {
        final boolean override = io.isElevatorOverrideRequested();
        return elevator.hasZeroed() || isTimedOut() || override;
    }

    protected void end()
    {
        elevator.setOpenLoop(0.0);
    }
}
