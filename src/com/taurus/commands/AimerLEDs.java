package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AimerLEDs extends Command
{
    final boolean ledsOn;
    
    public AimerLEDs(boolean ledsOn) 
    {
        requires(Robot.ledsSubsystem);
        this.ledsOn = ledsOn;
    }

    protected void initialize() 
    {
        Utilities.PrintCommand("LEDs", this);
        Robot.ledsSubsystem.enableLEDs(ledsOn);
        setTimeout(.25);
    }
    
    protected void execute() 
    {

    }

    protected boolean isFinished() 
    {
        return isTimedOut();
    }
    
    protected void end() 
    {
        
    }
    
    protected void interrupted() 
    {
        
    }
}
