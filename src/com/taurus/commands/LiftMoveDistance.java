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
        requires(Robot.liftSubsystem);
        this.distance = distance;
    }
    
    protected void initialize() {
        done = false;
        endHeight = (Robot.liftSubsystem.getHeightFromFloorAverage() + distance);
    }
    
    protected void execute() {
        Utilities.PrintCommand("Lift", this);
        done = Robot.liftSubsystem.setHeightFromFloor(endHeight);//store result of setHeight function into the done variable
    }

    protected boolean isFinished() {
        return done;
    }
    
    protected void end() {
    }
    
    protected void interrupted() {
        end();
    }
}