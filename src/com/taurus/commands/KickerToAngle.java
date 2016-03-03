package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class KickerToAngle extends Command
{
    final double KickerAngle;
    boolean done;
    
    public KickerToAngle(double desiredAngle)
    {
        requires(Robot.kickerSubsystem);
        KickerAngle = desiredAngle;
    }
    
    protected void initialize() 
    {
        done = false;
    }
    
    protected void execute() 
    {
        Utilities.PrintCommand("Kicker", this);
        done = Robot.kickerSubsystem.aimTo(KickerAngle);       
    }
    
    protected boolean isFinished() 
    {
        return done;
    }
    
    protected void end() 
    {
        
    }
    
    protected void interrupted() 
    {
        end();
    }
}
