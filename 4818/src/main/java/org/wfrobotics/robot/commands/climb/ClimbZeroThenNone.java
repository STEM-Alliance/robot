package org.wfrobotics.robot.commands.climb;

import org.wfrobotics.robot.subsystems.Climb;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public final class ClimbZeroThenNone extends ConditionalCommand
{
    public ClimbZeroThenNone()
    {
        super(new ClimbNone(), new ClimbZero());
    }

    protected boolean condition()
    {
        return Climb.getInstance().hasZeroed();
    }

    public static class ClimbZero extends CommandGroup
    {
        public ClimbZero()
        {
            addSequential(new ClimbGoHome());
        }
    }
}
