package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoPortCullis extends CommandGroup 
{
    public AutoPortCullis(AutoTurn.STATE_TURN position, boolean shoot)
    {
        addSequential(new AutoSetStartAngle(180));

        addParallel(new ManipulatorContinousTimeout(false, 1.5));
        addSequential(new AutoDrive(.5, -AutoDrive.SPEED_APPROACH*.75, false));
        //addParallel(new BallIntakeTimeout(true,1));
        addSequential(new AutoDrive(2.5, -AutoDrive.SPEED_APPROACH, false));
        if (position == AutoTurn.STATE_TURN.POSITION_TWO)
        {
            addSequential(new AutoDrive(1, -AutoDrive.SPEED_APPROACH, false));
        }
        //addParallel(new AimerContinuousTimeout(false, .5));
        // TODO - do something so AutoTurn is compensated for zero yaw being 180 degrees from our intended position
        //addSequential(new AutoDrive(1.45, 0.75, -0.75, false));  // Turn 180
        addSequential(new AutoTurn(position));
        if (shoot)
        {
            addSequential(new LiftToTop());
            addParallel(new LiftHold());
            addSequential(new ShooterFire());
        }
        else
        {
            addSequential(new ShooterFire(false));
        }
    }
}

