package org.wfrobotics.commands;

import org.wfrobotics.commands.drive.DriveSwerveHalo;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SteamworksDrive extends CommandGroup 
{    
    
    public SteamworksDrive() 
    {      
        
        addParallel(new IntakeSetup(true));
        addParallel(new IntakeSetup(true));
        //before this find out which one is on
        
        addSequential(new  DriveSwerveHalo() );
    }
    
    protected void end() {
        //turn off both intake motors
        
    }
    

}
