package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoPortCullisShoot extends CommandGroup 
{
    public AutoPortCullisShoot()
    {
        addParallel(new ManipulatorContinousTimeout(false, 1.5));
        addSequential(new AutoDrive(.5, -0.5, false, false));
        addParallel(new KickerContinuousTimeout(true,1));
        addSequential(new AutoDrive(4, -0.5, false, false));
        addParallel(new AimerContinuousTimeout(false, .5));
        addSequential(new AutoDrive(1.45, 0.75, -0.75, false, false));  // Turn 180
        addSequential(new LiftToTop());
        addParallel(new LiftHold());
        addSequential(new ShooterFire());
    }
}

