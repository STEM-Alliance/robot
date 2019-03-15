package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;


import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class SystemToCargoBay extends CommandGroup
{
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public SystemToCargoBay()
    {
            addParallel(new ElevatorToHeight(FieldHeight.CargoBay.getE()));
            addSequential(new LinkToHeight(FieldHeight.CargoBay.getL()));
    }

}
