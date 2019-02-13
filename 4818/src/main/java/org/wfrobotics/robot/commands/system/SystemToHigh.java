package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;
import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SystemToHigh extends CommandGroup
{
    public SystemToHigh()
    {
        final double hatchHeight = FieldHeight.HatchHigh.get();

        this.addParallel(new LinkToHeight(90.0));
        addSequential(new ElevatorToHeight(hatchHeight));
    }
}
