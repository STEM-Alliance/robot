package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class ElevatorShift extends InstantCommand
{
    private final Elevator elevator = Elevator.getInstance();
    private final boolean desired;

    public ElevatorShift(boolean liftNotClimb)
    {
        requires(elevator);
        desired = liftNotClimb;
    }

    protected void initialize()
    {
        elevator.setShifter(desired);
    }
}