package com.taurus.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoChevalDeFrise extends CommandGroup
{
    public AutoChevalDeFrise(AutoTurn.STATE_TURN position, boolean shoot)
    {
        // TODO - DRL get over it
        if (shoot)
        {
            addSequential(new AutoTurn(position));
            // TODO - DRL shoot or whatever
        }
    }
}
