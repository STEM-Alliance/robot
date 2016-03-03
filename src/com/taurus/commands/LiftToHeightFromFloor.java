package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class LiftToHeightFromFloor extends Command
{
    private boolean done;
    private double endHeight;
    
    /**
     * Move the lift to this height from the floor
     * @param height (signed inches)
     */
    public LiftToHeightFromFloor(double height) {
        requires(Robot.liftSubsystem);
        this.endHeight = height;
    }
    
    protected void initialize() {
        done = false;
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
