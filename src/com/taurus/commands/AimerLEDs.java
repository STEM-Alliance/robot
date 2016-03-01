package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AimerLEDs extends Command
{
    private boolean ledsOn;
    
    public AimerLEDs(boolean ledsOn) 
    {
        requires(Robot.aimerSubsystem);
        this.ledsOn = ledsOn;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() 
    {
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
        Utilities.PrintCommand("Aimer", this);
        Robot.aimerSubsystem.enableLEDs(ledsOn);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() 
    {
        return true;
    }
    
    // Called once after isFinished returns true
    protected void end() 
    {   
    }
    
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() 
    {
        
    }
}
