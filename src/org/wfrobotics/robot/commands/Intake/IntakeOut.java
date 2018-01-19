package org.wfrobotics.robot.commands.Intake;


import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class IntakeOut extends Command {

    public IntakeOut() {
        requires(Robot.intakeSubsystem);
    }

    protected void initialize() {
    }

    protected void execute() {
        double speed = -0.2;
        Robot.intakeSubsystem.setSpeed(speed); 
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
