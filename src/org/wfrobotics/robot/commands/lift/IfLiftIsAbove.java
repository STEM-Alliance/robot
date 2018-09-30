package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.subsystems.Lift;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class IfLiftIsAbove extends ConditionalCommand
{
    private final Lift lift = Lift.getInstance();
    private final double threshold;

    public IfLiftIsAbove(Command onTrue, double heightFromGround)
    {
        super(onTrue);
        threshold = heightFromGround;
    }

    protected boolean condition()
    {
        return lift.getPosition() > threshold;
    }
}
