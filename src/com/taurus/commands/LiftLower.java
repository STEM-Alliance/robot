package com.taurus.commands;

import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class LiftLower extends Command {
private boolean done;
private double endHeight;
    public LiftLower() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.liftSubsystem);
    }
    // Called just before this Command runs the first time
    protected void initialize() {
        done = false;
        double heightIncrement = 25.4;//mm = 1 in
        endHeight = (Robot.liftSubsystem.getHeight() - heightIncrement);
    }
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        done = Robot.liftSubsystem.setHeight(endHeight);//store result of setHeight function into the done variable
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return done;
    }
    // Called once after isFinished returns true
    protected void end() {
    }
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}