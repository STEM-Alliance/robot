package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class LoadBall extends CommandGroup
{
    private final double HEIGHT_GRAB = 25;  // TODO - DRL determine ideal lift height to grab ball on floor
    
    public LoadBall()
    {
        addSequential(new LiftToHeightFromFloor(HEIGHT_GRAB));
        addParallel(new LiftHold(.1, .3));
        addSequential(new AimerToAngle(45));
        addParallel(new LiftHold(.1, .3));
        addSequential(new ShooterGrab());
    }
}