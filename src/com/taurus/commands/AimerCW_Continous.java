package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AimerCW_Continous extends Command {

    public AimerCW_Continous() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.aimerSubsystem);
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Aimer", this);
        Robot.aimerSubsystem.setSpeed(.6);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
    
    // Called once after isFinished returns true
    protected void end() {
      Robot.aimerSubsystem.setSpeed(0);
      
    }
    
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}