package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ManipulatorContinous extends Command {
    boolean clockwise;

    public ManipulatorContinous(boolean clockwise) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.manipulatorSubsystem);
        this.clockwise = clockwise;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Manipulator", this);
        
        if(clockwise)
        {
            Robot.manipulatorSubsystem.setSpeed(.75);
        }
        else
        {
            Robot.manipulatorSubsystem.setSpeed(-.75);
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
    
    // Called once after isFinished returns true
    protected void end() {
      Robot.manipulatorSubsystem.setSpeed(0);
      
    }
    
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}