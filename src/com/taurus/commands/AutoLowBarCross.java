package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLowBarCross extends CommandGroup 
{
    public AutoLowBarCross()
    {
        addSequential(new AutoDrive(2.0, 0.5, false, false));
        addParallel(new KickerContinuousTimeout(true,1));
        addSequential(new ManipulatorContinousTimeout(false, 1.5));
        addSequential(new AutoDrive(3.0, 0.5, false, false));
       // addSequential(new AutoDrive(0.5, 0.5, -0.5, false, false));  // Turn right slightly
//        addSequential(new LiftToTop());
//        addSequential(new LiftHold());
//        addSequential(new ShooterFire());
//        addSequential(new AutoDrive(2.0, 0.5, -0.5, false, false));  // Turn 180 degrees
        // TODO - DRL does the above command turn about 180 degrees? Test it.
    }
}

