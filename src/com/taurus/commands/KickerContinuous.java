package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class KickerContinuous extends Command 
{
    boolean clockwise;
    
    public KickerContinuous(boolean clockwise) 
    {
        requires(Robot.kickerSubsystem);
        
        this.clockwise = clockwise;
    }
    
    protected void initialize() 
    {
        
    }
    
    protected void execute() 
    {
        Utilities.PrintCommand("Kicker", this);
        
        if(clockwise)
        {
            Robot.kickerSubsystem.setSpeed(-.5);
        }
        else
        {
            Robot.kickerSubsystem.setSpeed(.5);
        }
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