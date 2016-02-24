package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class LoadBall extends CommandGroup
{
    private final double HEIGHT_GRAB = 20;  // TODO - DRL determine ideal lift height to grab ball on floor
    
    public LoadBall()
    {
        addSequential(new LiftToHeight(HEIGHT_GRAB));
        addParallel(new LiftHold());
        addSequential(new AimerToAngle());
        addSequential(new ShooterGrab());
    }
}