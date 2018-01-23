package org.wfrobotics.prototype.commands;


import org.wfrobotics.prototype.Robot;


import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class BoxIn extends Command {
   
    
    
    public BoxIn() {
  
        requires(Robot.intakeSubsystem);
    }
    protected void initialize() {
    }

    
    protected void execute() {
        double speed = 0.5;
        
        Robot.intakeSubsystem.setSpeed(speed); 
        Robot.intakeSubsystem.holdBox();
        
    }


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
