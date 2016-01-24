package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ShooterFire extends CommandGroup {

    public ShooterFire() {
        //addParallel(new ShooterAim());  // TODO - DRL if we don't create a turret subsystem
        addSequential(new ShooterRev());
        addSequential(new ShooterFire());
    }
}