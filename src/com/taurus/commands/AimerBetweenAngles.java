package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AimerBetweenAngles extends Command
{
    final double minAngle;
    final double maxAngle;
    
    boolean done;
    
    /**
     * Go to between min and max
     * @param desiredAngle
     */
    public AimerBetweenAngles(double minAngle, double maxAngle)
    {
        requires(Robot.aimerSubsystem);
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
    }
    
    protected void initialize() 
    {
        done = false;
    }
    
    protected void execute() 
    {
        Utilities.PrintCommand("Aimer", this);
        done = Robot.aimerSubsystem.aimBetween(minAngle,maxAngle);    
    }

    protected boolean isFinished() 
    {
        double angle = Robot.aimerSubsystem.getCurrentAngle();
        if(angle + 3 > minAngle && angle - 3 < maxAngle)
            return true;
        else 
            return false;
    }
    
    protected void end() 
    {
              
    }
    
    protected void interrupted() 
    {
        
    }
}
