package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLowBar extends CommandGroup {
    public AutoLowBar()
    {
        //sequential move forward under low bar
        addSequential(new AutoDrive(5.0, 0.5, false, false));
        // raise lift to shooting height
        addSequential(new LiftToTop());
        addSequential(new LiftHold());
        // aim and shoot
        // TODO - DRL add aim when that works // addSequential(new Targeting());
        addSequential(new ShooterFire());
        //add turn around
    }
}

