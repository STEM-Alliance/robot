package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLowBar extends CommandGroup 
{
    public AutoLowBar()
    {
        addSequential(new AutoDrive(2.0, 0.5, false, false));
        addSequential(new KickerContinuousTimeout(true,1));
        addSequential(new ManipulatorContinousTimeout(false, 1.5));
        addSequential(new AutoDrive(3.0, 0.5, false, false));
        addSequential(new AutoDrive(1, 0.5, -0.5, false, false));  // Turn right slightly
        //addSequential(new LiftToTop());
        //addSequential(new LiftHold());
        addSequential(new ShooterFire());
        //addSequential(new AutoDrive(2.0, 0.5, -0.5, false, false));  // Turn 180 degrees
        // TODO - DRL does the above command turn about 180 degrees? Test it.
    }
}

