package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class LoadBallFromFloor extends CommandGroup
{
    public LoadBallFromFloor()
    {
        addSequential(new AimerToAngle());
        addSequential(new ShooterGrab());
    }
}