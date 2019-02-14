package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class SystemToLow extends ConditionalCommand
{
    private final Wrist wrist = Wrist.getInstance();

    public SystemToLow()
    {
        super(new SystemToCargo(), new SystemToHatch());
    }

    protected boolean condition()
    {
        return wrist.isCloserToCargoModeThanHatchMode();
    }

    private static class SystemToCargo extends CommandGroup
    {
        public SystemToCargo()
        {
            //        addParallel(new ElevatorToHeight(0.0));
            addSequential(new LinkToHeight(40.0));
        }
    }

    private static class SystemToHatch extends CommandGroup
    {
        public SystemToHatch()
        {
            //        addParallel(new ElevatorToHeight(0.0));
            addSequential(new LinkToHeight(21.0));
        }
    }
}
