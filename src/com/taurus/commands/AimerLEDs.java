package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AimerLEDs extends Command
{
    final boolean ledsOn;
    
    public AimerLEDs(boolean ledsOn) 
    {
        requires(Robot.aimerSubsystem);
        this.ledsOn = ledsOn;
    }

    protected void initialize() 
    {
        Utilities.PrintCommand("Aimer", this);
        Robot.aimerSubsystem.enableLEDs(ledsOn);
    }
    
    protected void execute() 
    {

    }

    protected boolean isFinished() 
    {
        return true;
    }
    
    protected void end() 
    {
        
    }
    
    protected void interrupted() 
    {
        
    }
}
