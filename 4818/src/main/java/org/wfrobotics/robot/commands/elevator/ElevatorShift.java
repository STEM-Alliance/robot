package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class ElevatorShift extends InstantCommand
{
    private final Elevator lift = Elevator.getInstance();
    private final boolean desired;

    public ElevatorShift(boolean liftNotClimb)
    {
        requires(lift);
        desired = liftNotClimb;
    }

    protected void initialize()
    {
        lift.setShifter(desired);
    }
}