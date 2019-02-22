package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class ElevatorZeroThenOpenLoop extends ConditionalCommand
{
    public ElevatorZeroThenOpenLoop()
    {
        super(new ElevatorOpenLoop(), new ElevatorZero());
    }

    protected boolean condition()
    {
        return Elevator.getInstance().hasZeroed();
    }
}
