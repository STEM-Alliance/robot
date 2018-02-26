package org.wfrobotics.robot.auto;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class AutoMove extends Command {

    public AutoMove() {
        requires(Robot.driveService.getSubsystem());
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        Robot.driveService.driveDistanceInit(3 * (6.5 * Math.PI));

    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {

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
