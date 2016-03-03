package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AimerBetweenAngles extends Command
{
    private final double minAngle;
    private final double maxAngle;
    private boolean done;
    
    /**
     * Go to between min and max
     * @param desiredAngle
     */
    public AimerBetweenAngles(double minAngle, double maxAngle)
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.aimerSubsystem);
        this.minAngle = minAngle;
        this.maxAngle = maxAngle;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {

        done = false;
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Aimer", this);
        done = Robot.aimerSubsystem.aimBetween(minAngle,maxAngle);    
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return done;
    }
    
    // Called once after isFinished returns true
    protected void end() {
              
    }
    
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
