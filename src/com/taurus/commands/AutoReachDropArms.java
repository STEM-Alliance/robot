package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoReachDropArms extends CommandGroup 
{
    public AutoReachDropArms()
    {
        addSequential(new AutoDrive(2.0, 0.5, false, false));
        addParallel(new KickerContinuousTimeout(true,1));
        addSequential(new ManipulatorContinousTimeout(false, 1.5));
    }
}

