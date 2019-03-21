package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;
import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SystemToCargoBay extends CommandGroup
{
    public SystemToCargoBay()
    {
            addParallel(new ElevatorToHeight(FieldHeight.CargoBay.getE()));
            addSequential(new LinkToHeight(FieldHeight.CargoBay.getL()));
    }
}
