package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.elevator.ElevatorGoHome;
import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;
import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SystemToLow extends CommandGroup
{
    public SystemToLow()
    {
        final double hatchHeight = FieldHeight.HatchLow.get();

        this.addParallel(new LinkToHeight(0.0));
        addSequential(new ElevatorToHeight(hatchHeight + 4.0));  // Gentler slamming
        addSequential(new ElevatorToHeight(hatchHeight));
        addSequential(new ElevatorGoHome(-0.25, 2.0));  // Anything the command didn't let the PID finish
    }
}
