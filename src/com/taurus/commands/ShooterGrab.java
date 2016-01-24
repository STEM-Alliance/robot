package com.taurus.commands;

import com.taurus.robot.OI;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ShooterGrab extends Command {
    
    public ShooterGrab() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.shooterSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        double speed = .38 + .5 * OI.getTriggerRight();
        Robot.shooterSubsystem.setSpeed(-speed, -speed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.shooterSubsystem.stopSwitch.get();
    }

    // Called once after isFinished returns true
    protected void end() {
        Robot.shooterSubsystem.setSpeed(0,0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
