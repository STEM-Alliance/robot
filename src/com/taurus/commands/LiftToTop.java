package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class LiftToTop extends Command {
    private boolean done;
    private double endHeight;

    public LiftToTop() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.liftSubsystem);
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
        done = false;
        endHeight = Preferences.getInstance().getDouble("LiftHighOffset", 40);//(Robot.liftSubsystem.LIMIT_UPPER);
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
        done = Robot.liftSubsystem.setHeightFromFloor(Robot.liftSubsystem.getHeightFromFloorAverage());
    }
    
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}