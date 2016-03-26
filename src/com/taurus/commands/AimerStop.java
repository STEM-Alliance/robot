package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.OI;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AimerStop extends Command 
{
    public AimerStop() 
    {
        requires(Robot.aimerSubsystem);
    }
    
    protected void initialize() 
    {
        
    }
    
    protected void execute() 
    {
        Utilities.PrintCommand("Aimer", this);
        Robot.aimerSubsystem.setSpeed(OI.getAimerY()*.325);
    }

    protected boolean isFinished() 
    {
        return false;
    }
    
    protected void end() 
    {
        Robot.aimerSubsystem.setSpeed(0);      
    }
    
    protected void interrupted() 
    {
        
    }
}