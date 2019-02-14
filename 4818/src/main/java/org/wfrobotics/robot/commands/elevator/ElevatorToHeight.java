package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.Command;

public class ElevatorToHeight extends Command
{
    private final Elevator elevator = Elevator.getInstance();
    private final IO io = IO.getInstance();
    protected final double desired;

    public ElevatorToHeight(double desired)
    {
        requires(elevator);
        this.desired = desired;
    }

    protected void initialize()
    {
        elevator.setClosedLoop(desired);
    }

    protected boolean isFinished()
    {
        final boolean isClose = Math.abs(elevator.getPosition() - desired) < 1.0;
        return isClose || io.isElevatorOverrideRequested();
    }

    protected void end()
    {
        elevator.setOpenLoop(0.0);  // In autonomous, this holds the current position
    }
}
