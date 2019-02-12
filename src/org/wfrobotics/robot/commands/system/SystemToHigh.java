package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;
import org.wfrobotics.robot.config.FieldHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SystemToHigh extends CommandGroup
{
    public SystemToHigh()
    {
        final double hatchHeight = FieldHeight.HatchHigh.get();

        // TODO Other subsystems in parallel
        addSequential(new ElevatorToHeight(hatchHeight));
    }
}
