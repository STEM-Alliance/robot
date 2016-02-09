package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ShooterFire extends CommandGroup {

    public ShooterFire() {
        addParallel(new ShooterRev());
        addSequential(new Targeting());
        addSequential(new ShooterRelease());
    }
}