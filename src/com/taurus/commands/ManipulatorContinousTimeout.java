package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ManipulatorContinousTimeout extends Command 
{
    boolean clockwise;

    double time;
    
    public ManipulatorContinousTimeout(boolean clockwise, double time) 
    {
        requires(Robot.manipulatorSubsystem);
        this.clockwise = clockwise;
        this.time = time;
    }
    
    protected void initialize() 
    {
        setTimeout(time);
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
        return isTimedOut();
    }
    
    protected void end() 
    {
        Robot.manipulatorSubsystem.setSpeed(0);     
    }
    
    protected void interrupted() 
    {
        
    }
}