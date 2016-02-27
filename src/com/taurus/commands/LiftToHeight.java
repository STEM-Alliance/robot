package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class LiftToHeight extends Command
{
    private boolean done;
    private double endHeight;
    
    /**
     * Move the lift to this height from the floor
     * @param height (signed inches)
     */
    public LiftToHeight(double height) {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.liftSubsystem);
        this.endHeight = height;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
        done = false;
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
