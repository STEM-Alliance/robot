package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLowBarShoot extends CommandGroup 
{
    public AutoLowBarShoot()
    {
        addSequential(new AutoDrive(.5, 0.5, false, false));
        addParallel(new KickerContinuousTimeout(true,1));
        addParallel(new ManipulatorContinousTimeout(false, 1.5));
        addSequential(new AutoDrive(3.75, 0.5, false, false));
        addParallel(new AimerContinuousTimeout(false, .5));
        addSequential(new AutoDrive(1, 0.5, -0.5, false, false));  // Turn right slightly
        addSequential(new LiftToTop());
        addParallel(new LiftHold());
        addSequential(new ShooterFire());
        //addSequential(new AutoDrive(2.0, 0.5, -0.5, false, false));  // Turn 180 degrees
        // TODO - DRL does the above command turn about 180 degrees? Test it.
    }
}

