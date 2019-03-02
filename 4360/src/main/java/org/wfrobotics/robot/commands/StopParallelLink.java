package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.IntakeMotor;
import org.wfrobotics.robot.subsystems.ParallelLink;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class StopParallelLink extends Command {

	
	
    public StopParallelLink() {
       requires(ParallelLink.getInstance());
    }

    // Called just before this Command runs the first time
    protected void initialize() {

    	ParallelLink.getInstance().setlink(0);
    	
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
