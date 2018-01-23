package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;


import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeStop extends Command {
    
    public IntakeStop() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.intakeSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        double speed = 0;
        Robot.intakeSubsystem.setSpeed(speed); 
        Robot.intakeSubsystem.holdBox();
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
