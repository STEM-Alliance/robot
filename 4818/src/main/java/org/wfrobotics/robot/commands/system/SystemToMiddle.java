package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;


import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class SystemToMiddle extends ConditionalCommand
{
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public SystemToMiddle()
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
            addParallel(new ElevatorToHeight(0.0));
            addSequential(new LinkToHeight(90.0));
        }
    }

    private static class SystemToHatch extends CommandGroup
    {
        public SystemToHatch()
        {
            addParallel(new ElevatorToHeight(0.0));
            addSequential(new LinkToHeight(90.0));
        }
    }
}
