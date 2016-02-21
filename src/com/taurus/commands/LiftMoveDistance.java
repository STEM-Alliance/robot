package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class LiftMoveDistance extends Command {
    private boolean done;
    private double endHeight;
    private double distance;
    
    /**
     * Move the lift this distance, either up or down based on sign of distance.
     * Ex: distance = -1 -> Move down one inch
     * @param distance (signed inches)
     */
    public LiftMoveDistance(double distance) {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.liftSubsystem);
        this.distance = distance;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
        done = false;
        endHeight = (Robot.liftSubsystem.getHeightFromFloorAverage() + distance);
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Lift", this);
        done = Robot.liftSubsystem.setHeightFromFloor(endHeight);//store result of setHeight function into the done variable
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