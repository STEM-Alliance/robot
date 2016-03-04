package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ManipulatorContinous extends Command 
{
    boolean clockwise;

    public ManipulatorContinous(boolean clockwise) 
    {
        requires(Robot.manipulatorSubsystem);
        this.clockwise = clockwise;
    }
    
    protected void initialize() 
    {
        
    }
    
    protected void execute()
    {
        Utilities.PrintCommand("Manipulator", this);
        
        if(clockwise)
        {
            Robot.manipulatorSubsystem.setSpeed(.5);
        }
        else
        {
            Robot.manipulatorSubsystem.setSpeed(-.5);
        }
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