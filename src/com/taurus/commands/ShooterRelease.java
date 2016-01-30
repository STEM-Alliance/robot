package com.taurus.commands;

import com.taurus.robot.Robot;
import com.taurus.subsystems.ShooterSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class ShooterRelease extends Command {
    
    public ShooterRelease() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.shooterSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        // Assume Shooter speed is set by prior command
        Robot.shooterSubsystem.setBallRelease(true);
        setTimeout(2);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        // Once the ball is released (based on timeout), we should retract the servo to end in a safe state
        if (isTimedOut())
        {
            Robot.shooterSubsystem.setBallRelease(false);
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return isTimedOut() && Robot.shooterSubsystem.getBallRelease() == ShooterSubsystem.BALL_RELEASE_STATE.CONTRACTED;
    }
                
    // Called once after isFinished returns true
    protected void end() {
        Robot.shooterSubsystem.setSpeed(0,0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
