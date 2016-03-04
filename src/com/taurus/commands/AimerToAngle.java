package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AimerToAngle extends Command
{
    private final double AIMER_LOAD_BALL;
    
    public AimerToAngle() 
    {
        requires(Robot.aimerSubsystem);
        AIMER_LOAD_BALL = Robot.aimerSubsystem.ANGLE_GRAB_FROM_BOTTOM_FRONT;
    }
    
    public AimerToAngle(double desiredAngle)
    {
        requires(Robot.aimerSubsystem);
        AIMER_LOAD_BALL = desiredAngle;
    }
    
    protected void initialize() 
    {
        
    }
    
    protected void execute() 
    {
        Utilities.PrintCommand("Aimer", this);
        Robot.aimerSubsystem.aimTo(AIMER_LOAD_BALL);       
    }

    protected boolean isFinished() 
    {
        return Math.abs(Robot.aimerSubsystem.getCurrentAngle() - AIMER_LOAD_BALL) < 1;
    }
    
    protected void end() 
    {
       Robot.aimerSubsystem.setSpeed(0);
    }
    
    protected void interrupted() 
    {
        
    }
}
