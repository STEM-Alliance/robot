package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.IntakeMotor;
import org.wfrobotics.robot.subsystems.ParallelLink;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class ParallelLinkControl extends Command {
	double speed;
    public ParallelLinkControl(double speed) {
    	{
    		
    		this.speed = speed;
    		requires(ParallelLink.getInstance());
    		
        	
    	}
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {

        ParallelLink.getInstance().setlink(speed);
    }


	// Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }


}
