package org.wfrobotics.prototype.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoTurnAndTurn extends CommandGroup
{
    public AutoTurnAndTurn()
    {
        this.addSequential(new AutoDrive2(.25, .1, 3));
        this.addSequential(new AutoDrive2(-.25, .1, 3));
    }
}
