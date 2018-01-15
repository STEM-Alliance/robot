package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.subsystems.BoxIntakeSubsystem;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class Wait extends Command {
    BoxIntakeSubsystem boxIntakeSub = new BoxIntakeSubsystem();
    public Wait() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        int speed = 0;
        boxIntakeSub.leftSetSpeed(speed); 
        boxIntakeSub.rightSetSpeed(speed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
