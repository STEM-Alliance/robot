package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;
import org.wfrobotics.robot.commands.intake.CargoIn;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SystemIntakeCargo extends CommandGroup
{
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public SystemIntakeCargo()
    {
        addParallel(new ElevatorToHeight(0));
        addSequential(new LinkToHeight(98));
        addSequential(new CargoIn());
    }

}
