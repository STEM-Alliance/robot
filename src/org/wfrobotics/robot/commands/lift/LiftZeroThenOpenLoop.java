package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class LiftZeroThenOpenLoop extends ConditionalCommand
{
    public LiftZeroThenOpenLoop()
    {
        super(new LiftOpenLoop(), new AutoZero());
    }

    protected boolean condition()
    {
        return LiftSubsystem.getInstance().hasZeroed();
    }
}
