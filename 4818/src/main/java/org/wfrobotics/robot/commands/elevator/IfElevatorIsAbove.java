package org.wfrobotics.robot.commands.elevator;

import org.wfrobotics.robot.subsystems.Elevator;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class IfElevatorIsAbove extends ConditionalCommand
{
    private final Elevator lift = Elevator.getInstance();
    private final double threshold;

    public IfElevatorIsAbove(Command onTrue, double heightFromGround)
    {
        super(onTrue);
        threshold = heightFromGround;
    }

    protected boolean condition()
    {
        return lift.getPosition() > threshold;
    }
}
