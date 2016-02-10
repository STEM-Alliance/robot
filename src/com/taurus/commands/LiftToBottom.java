package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class LiftToBottom extends Command {
    private boolean done;
    private double endHeight;
    
    public LiftToBottom() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.liftSubsystem);
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
        done = false;
        endHeight = (Robot.liftSubsystem.LIMIT_LOWER);
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Lift", this);
        done = Robot.liftSubsystem.setHeight(endHeight);//store result of setHeight function into the done variable
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return done;
    }
    
    // Called once after isFinished returns true
    protected void end() {
        done = Robot.liftSubsystem.setHeight(Robot.liftSubsystem.getHeightAverageTotal());
    }
    
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}