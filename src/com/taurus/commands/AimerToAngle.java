package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AimerToAngle extends Command
{
    private final double AIMER_LOAD_BALL;
    
    /**
     * Go to angle to load ball
     */
    public AimerToAngle() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.aimerSubsystem);
        AIMER_LOAD_BALL = 60;  // TODO - DRL determine ideal angle to load ball from floor though testing
    }
    
    /**
     * Go to desiredAngle
     * @param desiredAngle
     */
    public AimerToAngle(double desiredAngle)
    {
        AIMER_LOAD_BALL = desiredAngle;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
        
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Aimer", this);
        Robot.aimerSubsystem.aim(AIMER_LOAD_BALL - Robot.aimerSubsystem.getCurrentAngle());       
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Math.abs(Robot.aimerSubsystem.getCurrentAngle() - AIMER_LOAD_BALL) < 3;
    }
    
    // Called once after isFinished returns true
    protected void end() {
              
    }
    
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
