package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.Command;

public class ElevatorToHeight extends Command
{
    private final Elevator lift = Elevator.getInstance();
    private final IO io = IO.getInstance();
    protected final double desired;

    public ElevatorToHeight(double desired)
    {
        requires(lift);
        this.desired = desired;
    }

    protected void initialize()
    {
        lift.setClosedLoop(desired);
    }

    protected boolean isFinished()
    {
        final boolean isClose = Math.abs(lift.getPosition() - desired) < 1.0;
        return isClose || io.isElevatorOverrideRequested();
    }

    protected void end()
    {
        lift.setOpenLoop(0.0);  // In autonomous, this holds the current position
    }
}
