package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLowBar extends CommandGroup 
{
    public AutoLowBar(boolean shoot)
    {
        addSequential(new AutoDrive(.5, 0.5, false, false));
        addParallel(new KickerContinuousTimeout(true,1));
        addParallel(new ManipulatorContinousTimeout(false, 1.5));
        addSequential(new AutoDrive(4.25, 0.6, false, false));
        if (shoot)
        {
            addParallel(new AimerContinuousTimeout(false, .5));
            addSequential(new AutoDrive(1, 0.5, -0.5, false, false));  // Turn right slightly
            addSequential(new LiftToTop());
            addParallel(new LiftHold());
            addSequential(new ShooterFire());
            addSequential(new LiftToBottom());
        }
        addSequential(new AutoDrive(1.45, 0.75, -0.75, false, false));  // Turn 180
    }
}

