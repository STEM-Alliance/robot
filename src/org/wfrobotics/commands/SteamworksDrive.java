package org.wfrobotics.commands;

import org.wfrobotics.commands.drive.DriveSwerveHalo;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class SteamworksDrive extends CommandGroup 
{    
    
    public SteamworksDrive() 
    {   
        // TODO before this find out which one is on
        //addParallel(new IntakeSetup(true));  // TODO use newer parameters
        //addParallel(new IntakeSetup(true));  // TODO use newer parameters
        // TODO LEDs? Make them also depend on our pathing?        
        addSequential(new DriveSwerveHalo());
    }
    
    protected void end() {
        // TODO turn off both intake motors
    }
    
    // TODO what if we are interrupted?
}
