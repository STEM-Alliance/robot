package com.taurus.commands;

import com.taurus.robot.OI;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ShooterRev extends Command {
    
    public ShooterRev() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.shooterSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        // We want a constant fire speed, thus set the speed only once
        // If we have a vision, move this to execute and do calculation based on vision
        double speedTop = .5 + .1 * OI.getTriggerRight();
        double speedBottom = .7 + .1 * OI.getTriggerRight();
        Robot.shooterSubsystem.setSpeed(speedTop, speedBottom);
        setTimeout(1);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return isTimedOut();
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
