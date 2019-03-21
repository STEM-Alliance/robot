package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;
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
        return superStructure.getHasCargo();
    }

    private static class SystemToCargo extends CommandGroup
    {
        public SystemToCargo()
        {
            // addSequential(new IfElevatorIsAbove(new ElevatorToHeight(FieldHeight.CargoLow.getE() + 4.0), 10.0));
            addSequential(new ElevatorToHeight(FieldHeight.CargoLow.getE()));
            addSequential(new LinkToHeight(FieldHeight.CargoLow.getL()));
        }
    }

    private static class SystemToHatch extends CommandGroup
    {
        public SystemToHatch()
        {
            addSequential(new ElevatorToHeight(FieldHeight.HatchLow.getE()));
            addSequential(new LinkToHeight(FieldHeight.HatchLow.getL()));
        }
    }
}
