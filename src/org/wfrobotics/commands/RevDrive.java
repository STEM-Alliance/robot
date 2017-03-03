package org.wfrobotics.commands;

import org.wfrobotics.commands.Rev.MODE;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class RevDrive extends CommandGroup {

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
