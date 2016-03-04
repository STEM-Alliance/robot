package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class KickerContinuousTimeout extends Command 
{
    boolean clockwise;
    double time;
    double startTime;
    
    public KickerContinuousTimeout(boolean clockwise, double time) 
    {
        requires(Robot.kickerSubsystem);
        
        this.time = time;
        this.clockwise = clockwise;
    }
    
    protected void initialize() 
    {
        startTime = Timer.getFPGATimestamp();
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
        return Timer.getFPGATimestamp() - startTime > time;
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