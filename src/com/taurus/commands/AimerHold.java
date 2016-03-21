package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AimerHold extends Command
{
    private double AIMER_LOAD_BALL;
    
    public AimerHold() 
    {
        requires(Robot.aimerSubsystem);
    }
    
    protected void initialize() 
    {
        AIMER_LOAD_BALL = Robot.aimerSubsystem.getCurrentAngle();
    }
    
    protected void execute() 
    {
        Utilities.PrintCommand("Aimer", this);
        Robot.aimerSubsystem.aimTo(AIMER_LOAD_BALL);       
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
