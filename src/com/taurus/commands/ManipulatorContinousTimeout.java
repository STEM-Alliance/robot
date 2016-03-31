package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class ManipulatorContinousTimeout extends Command 
{
    boolean clockwise;

    double time;
    double startTime;
    
    public ManipulatorContinousTimeout(boolean clockwise, double time) 
    {
        requires(Robot.manipulatorSubsystem);
        this.clockwise = clockwise;
        this.time = time;
    }
    
    protected void initialize() 
    {
        startTime = Timer.getFPGATimestamp();
    }
    
    protected void execute()
    {
        Utilities.PrintCommand("Manipulator", this);
        
        if(clockwise)
        {
            Robot.manipulatorSubsystem.setSpeed(.65);
        }
        else
        {
            Robot.manipulatorSubsystem.setSpeed(-.65);
        }
    }

    protected boolean isFinished() 
    {
        return Timer.getFPGATimestamp() - startTime > time;
    }
    
    protected void end() 
    {
        Robot.manipulatorSubsystem.setSpeed(0);     
    }
    
    protected void interrupted() 
    {
        
    }
}