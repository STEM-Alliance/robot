package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoTerrain extends CommandGroup 
{
    public AutoTerrain()
    {
        addSequential(new AutoDrive(5.0, 0.20, true, true));
        addSequential(new LiftToTop());
        addSequential(new LiftHold());
        // TODO - DRL add aim when that works // addSequential(new Targeting());
        addSequential(new ShooterFire());
        addSequential(new AutoDrive(2.0, 0.5, -0.5, false, false));
        // TODO - DRL does the above command turn about 180 degrees? Test it.
    }
}

