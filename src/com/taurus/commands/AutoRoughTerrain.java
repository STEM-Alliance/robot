package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoRoughTerrain extends CommandGroup 
{
    public AutoRoughTerrain(AutoTurn.STATE_TURN position, boolean shoot)
    {
        addSequential(new AutoSetStartAngle(180));
        //TODO adjust time parameters
        addSequential(new AutoDrive(.5, -AutoDrive.SPEED_APPROACH, false));//reach defense
        addParallel(new AimerContinuousTimeout(false, .7));
        addSequential(new AutoDrive(1.5, -AutoDrive.SPEED_APPROACH, false));//reach defense
        addSequential(new AutoDrive(.75, -1.0, false));  //burst over defense
        addSequential(new AutoDrive(.25, -AutoDrive.SPEED_APPROACH, false));  // ensure over defense
        //addParallel(new KickerContinuousTimeout(true, 1));
        addSequential(new ManipulatorContinousTimeout(false, 1.5));
        
        if (position == AutoTurn.STATE_TURN.POSITION_TWO)
        {
            addSequential(new AutoDrive(.5, -AutoDrive.SPEED_APPROACH, false));
        }
        //addParallel(new AimerContinuousTimeout(false, .5));
        addSequential(new AutoTurn(position));
        
        if (shoot)
        {
            addSequential(new AutoLiftAndShoot());
        }
        else
        {
            addSequential(new ShooterFire(false));
        }
    }
}

