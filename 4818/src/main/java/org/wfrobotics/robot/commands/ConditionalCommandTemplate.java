package org.wfrobotics.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public final class ConditionalCommandTemplate extends ConditionalCommand
{
    public ConditionalCommandTemplate()
    {
        super(new CommandTemplate(), new ClimbZero());
    }

    /**
     * If the sensor has zeroed (returned true)
     */
    protected boolean condition()
    {
        // return Climb.getInstance().hasZeroed();
        return true;
    }

    public static class ClimbZero extends CommandGroup
    {
        public ClimbZero()
        {
            // addSequential(new ClimbGoHome());
        }
    }
}
