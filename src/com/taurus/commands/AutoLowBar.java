package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLowBar extends CommandGroup {
    public AutoLowBar()
    {
        //sequential move forward under low bar
        addSequential(new AutoDrive(5.0, 0.5, false, false));
        // aim and shoot
        addSequential(new ShooterFire());
        //add turn around
    }
}

