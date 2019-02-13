package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;
import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SystemToMiddle extends CommandGroup
{
    public SystemToMiddle()
    {
        final double hatchHeight = FieldHeight.HatchHigh.get();

        this.addParallel(new LinkToHeight(45.0));
        this.addSequential(new ElevatorToHeight(hatchHeight));
    }
}
