package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.commands.elevator.ElevatorGoHome;
import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class SystemPickup extends ConditionalCommand
{
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public SystemPickup()
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
            addSequential(new LinkToHeight(105.0));
            addSequential(new ElevatorToHeight(0.0));
        }
    }

    private static class SystemToHatch extends CommandGroup
    {
        public SystemToHatch()
        {
            addSequential(new LinkToHeight(FieldHeight.HatchLow.getL()));
            addSequential(new ElevatorToHeight(FieldHeight.HatchLow.getE()));
            
        }
    }
}
