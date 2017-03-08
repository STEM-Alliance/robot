package org.wfrobotics.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class RevDrive extends CommandGroup
{
    public RevDrive(Rev.MODE mode)
    {
        addParallel(new SteamworksDrive());
        addSequential(new Rev(mode));
    }
    
    public RevDrive(Rev.MODE mode, double timeout)
    {
        addParallel(new SteamworksDrive());
        addSequential(new Rev(mode, timeout));
    }
}
