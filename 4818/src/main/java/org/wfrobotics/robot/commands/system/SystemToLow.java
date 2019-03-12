package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class SystemToLow extends ConditionalCommand
{
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public SystemToLow()
    {
        super(new SystemToCargo(), new SystemToHatch());
    }

    protected boolean condition()
    {
        return !superStructure.getHasHatch();
    }

    private static class SystemToCargo extends CommandGroup
    {
        public SystemToCargo()
        {
            if (SuperStructure.getInstance().getHasCargo())
            {
                addParallel(new ElevatorToHeight(0.0));
                addSequential(new LinkToHeight(40.0));
            }
            else
            {
                addSequential(new LinkToHeight(95.5));
            }
        }
    }

    private static class SystemToHatch extends CommandGroup
    {
        public SystemToHatch()
        {
                addSequential(new LinkToHeight(95.5));
                addSequential(new ElevatorToHeight(0.0));
        }
    }
}
