package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoRoughTerrain extends CommandGroup 
{
    public AutoRoughTerrain(AutoTurn.STATE_TURN position, boolean shoot)
    {
        addSequential(new AutoSetYawZero(0));
        //TODO adjust time parameters
        addSequential(new AutoDrive(2, AutoDrive.SPEED_APPROACH, false));//reach defense
        addSequential(new AutoDrive(3, 1.0, false));//over defense shooter up?
        addParallel(new KickerContinuousTimeout(true, 1));
        addSequential(new ManipulatorContinousTimeout(false, 1.5));
        if (position == AutoTurn.STATE_TURN.POSITION_TWO)
        {
            addSequential(new AutoDrive(1, AutoDrive.SPEED_APPROACH, false));
        }
        addParallel(new AimerContinuousTimeout(false, .5));
        addSequential(new AutoTurn(position));
        
        if (shoot)
        {
            addSequential(new LiftToTop());
            addParallel(new LiftHold());
            addSequential(new ShooterFire());
            addSequential(new LiftToBottom());
            //TODO turn to 0 yaw
            //TODO raise manipulator and kicker?
            //TODO go backwards over defense?
        }
    }
}

