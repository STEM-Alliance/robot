package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class BallIntake extends Command 
{
    boolean in;
    
    public BallIntake(boolean in) 
    {
        requires(Robot.kickerSubsystem);
        
        this.in = in;
    }
    
    protected void initialize() 
    {
        
    }
    
    protected void execute() 
    {
        Utilities.PrintCommand("Kicker", this);
        
        if(in)
        {
            Robot.kickerSubsystem.setSpeed(-1);
        }
        else
        {
            Robot.kickerSubsystem.setSpeed(1);
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