package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;
import org.wfrobotics.robot.config.FieldHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SystemToMiddle extends CommandGroup
{
    public SystemToMiddle()
    {
        final double hatchHeight = FieldHeight.HatchHigh.get();

        // TODO Other subsystems in parallel
        this.addSequential(new ElevatorToHeight(hatchHeight));
    }
}
