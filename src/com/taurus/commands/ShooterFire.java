package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ShooterFire extends CommandGroup {

    public ShooterFire() {
        addSequential(new ShooterRev());
        // TODO - DRL add once this works //addSequential(new Targeting());
        addSequential(new ShooterRelease());
        // sequential stop
    }
}                           