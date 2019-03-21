package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;
import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SystemToCargoBoxPickup extends CommandGroup
{
    public SystemToCargoBoxPickup()
    {
            addParallel(new ElevatorToHeight(FieldHeight.CargoBoxPickup.getE()));
            addSequential(new LinkToHeight(FieldHeight.CargoBoxPickup.getL()));
    }
}
