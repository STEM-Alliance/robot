package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoReachDropArms extends CommandGroup 
{
    public AutoReachDropArms(boolean driveDirectionForwards)
    {
        double speed = (driveDirectionForwards) ? .5 : -.5;
        
        addSequential(new AutoDrive(2.0, speed, false));
        //addParallel(new BallIntakeTimeout(true,1));
        addSequential(new ManipulatorContinousTimeout(false, 1.5));        
    }
}

