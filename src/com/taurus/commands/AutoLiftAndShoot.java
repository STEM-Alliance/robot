package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLiftAndShoot extends CommandGroup 
{
    public AutoLiftAndShoot()
    {
        addSequential(new LiftToTop());
        addParallel(new LiftHold());
        addSequential(new ShooterFire());
        addSequential(new LiftToBottom());
    }
}

