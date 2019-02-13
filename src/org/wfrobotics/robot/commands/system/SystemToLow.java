package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SystemToLow extends CommandGroup
{
    public SystemToLow()
    {
        //        addParallel(new ElevatorToHeight(0.0));
        addSequential(new LinkToHeight(21.0));
    }
}
