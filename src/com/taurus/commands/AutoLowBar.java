package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoLowBar extends CommandGroup 
{
    public AutoLowBar(boolean shoot)
    {
        addSequential(new AutoSetYawZero(0));
        addSequential(new AutoDrive(.5, AutoDrive.SPEED_APPROACH,false));
        addParallel(new KickerContinuousTimeout(true,1));
        addParallel(new ManipulatorContinousTimeout(false, 1.5));
        addSequential(new AutoDrive(4.25, AutoDrive.SPEED_APPROACH, false));
        addParallel(new AimerContinuousTimeout(false, .5));
        addSequential(new AutoTurn(AutoTurn.STATE_TURN.POSITION_ONE));  // Turn right slightly
        
        if (shoot)
        {
            addSequential(new LiftToTop());
            addParallel(new LiftHold());
            addSequential(new ShooterFire());
            addSequential(new LiftToBottom());
            addSequential(new AutoDrive(1.45, 0.75, -0.75, false));  // Turn 180
        }
    }
}

