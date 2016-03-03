package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ManipulatorStop extends Command 
{
    public ManipulatorStop()
    {
        requires(Robot.manipulatorSubsystem);
    }
    
    protected void initialize() 
    {
        
    }
    
    protected void execute()
    {
        Utilities.PrintCommand("Manipulator", this);
        Robot.manipulatorSubsystem.setSpeed(0);
    }

    protected boolean isFinished() 
    {
        return false;
    }

    protected void end() 
    {
        Robot.manipulatorSubsystem.setSpeed(0);
      
    }
    
    protected void interrupted() 
    {
        
    }
}