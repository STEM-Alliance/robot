package org.wfrobotics.robot.commands.lift;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class AutoZero extends CommandGroup
{
    private class ConditionalDown extends ConditionalCommand
    {
        public ConditionalDown()
        {
            super(new LiftGoHome(-.3, 10));
        }

        protected boolean condition()
        {
            return !LiftGoHome.everZeroed();
        }
    }

    public AutoZero()
    {
        this.addSequential(new LiftGoHome(.3, 2));
        this.addSequential(new ConditionalDown());
    }
}
