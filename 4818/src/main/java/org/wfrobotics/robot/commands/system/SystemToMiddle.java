package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;
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
            addParallel(new ElevatorToHeight(FieldHeight.CargoMiddle.getE()));
            addSequential(new LinkToHeight(FieldHeight.CargoMiddle.getL()));
        }
    }

    private static class SystemToHatch extends CommandGroup
    {
        public SystemToHatch()
        {
            addParallel(new ElevatorToHeight(FieldHeight.HatchMiddle.getE()));
            addSequential(new LinkToHeight(FieldHeight.HatchMiddle.getL()));
        }
    }
}
