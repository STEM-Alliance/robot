package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.subsystems.Lift;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class shift extends InstantCommand
{
    private final Lift lift = Lift.getInstance();
    private final boolean desired;

    public shift(boolean out)
    {
        requires(lift);
        desired = out;
    }

    protected void initialize()
    {
        lift.setPoppers(desired);
    }
}
