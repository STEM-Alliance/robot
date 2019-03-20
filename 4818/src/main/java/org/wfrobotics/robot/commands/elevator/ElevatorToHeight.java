package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.Command;

public class ElevatorToHeight extends Command
{
    private static final double kHeightOnTargetInches = 1.0;

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
        final boolean isSlowEnough = elevator.onTarget();
        final boolean isClose = Math.abs(elevator.getPosition() - desired) < kHeightOnTargetInches;
        return isClose || isSlowEnough || io.isElevatorOverrideRequested();
    }

    protected void end()
    {
        elevator.setOpenLoop(0.0);
    }
}
