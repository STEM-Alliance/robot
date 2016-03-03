package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class KickerStop extends Command 
{
    public KickerStop() 
    {
        requires(Robot.kickerSubsystem);
    }
    
    protected void initialize() 
    {
        
    }
    
    protected void execute()
    {
        Utilities.PrintCommand("Kicker", this);
        Robot.kickerSubsystem.setSpeed(0);
    }

    protected boolean isFinished() 
    {
        return false;
    }
    
    protected void end() 
    {
        Robot.kickerSubsystem.setSpeed(0);      
    }
    
    protected void interrupted() 
    {
        end();
    }
}