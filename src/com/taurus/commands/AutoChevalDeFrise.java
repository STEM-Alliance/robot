package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoChevalDeFrise extends CommandGroup
{
    public AutoChevalDeFrise(AutoTurn.STATE_TURN position, boolean shoot)
    {
        addSequential(new AutoSetStartAngle(180));

        addSequential(new AutoDrive(.5, -AutoDrive.SPEED_APPROACH, false));
        addSequential(new ManipulatorContinousTimeout(false, 1.5));
        addSequential(new AutoDrive(4, -AutoDrive.SPEED_APPROACH, false));
        if (position == AutoTurn.STATE_TURN.POSITION_TWO)
        {
            addSequential(new AutoDrive(1, -AutoDrive.SPEED_APPROACH, false));
        }
        addSequential(new KickerContinuousTimeout(true,1));
        addParallel(new AimerContinuousTimeout(false, .5));//move aimer to detect target
        addSequential(new AutoTurn(position));
        if (shoot)
        {
            addSequential(new LiftToTop());
            addParallel(new LiftHold());
            addSequential(new ShooterFire());
            addSequential(new AutoTurn(0.0));
        }
        
    }
}
