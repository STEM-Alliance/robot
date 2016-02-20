package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoTerrain extends CommandGroup 
{
    public AutoTerrain()
    {
        //drive forward
        addSequential(new AutoDriveTank(5.0, 0.25, true));
        // aim and shoot
        addSequential(new ShooterFire());
        // add turn around
    }
}

