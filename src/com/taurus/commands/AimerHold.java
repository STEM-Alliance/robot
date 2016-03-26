package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.OI;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class AimerHold extends Command
{
    private double AIMER_LOAD_BALL;
    private double startTime;
    
    public AimerHold() 
    {
        requires(Robot.aimerSubsystem);
    }
    
    protected void initialize() 
    {
        AIMER_LOAD_BALL = Robot.aimerSubsystem.getCurrentAngle();
        startTime = Timer.getFPGATimestamp();
    }
    
    protected void execute() 
    {
        Utilities.PrintCommand("Aimer", this);
        
//        if(Math.abs(OI.getAimerY()) < 0.01)
//        {
//            if(Timer.getFPGATimestamp() - startTime > 3)
//            {
//                Robot.aimerSubsystem.aimTo(AIMER_LOAD_BALL);
//            }
//            else
//            {
//                Robot.aimerSubsystem.setSpeed(0);
//            }
//        }
//        else
        {
            startTime = Timer.getFPGATimestamp();
            AIMER_LOAD_BALL = Robot.aimerSubsystem.getCurrentAngle();
            Robot.aimerSubsystem.setSpeed(OI.getAimerY()*.325);
        }
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
