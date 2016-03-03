package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class LiftToTop extends Command {
    private boolean done;
    private double endHeight;

    public LiftToTop() {
        requires(Robot.liftSubsystem);
    }
    
    protected void initialize() {
        done = false;
        endHeight = Preferences.getInstance().getDouble("LiftHighOffset", 40);
    }
    
    protected void execute() {
        Utilities.PrintCommand("Lift", this);
        done = Robot.liftSubsystem.setHeightFromFloor(endHeight);//store result of setHeight function into the done variable
    }

    protected boolean isFinished() {
        return done;
    }
    
    protected void end() {
        done = Robot.liftSubsystem.setHeightFromFloor(Robot.liftSubsystem.getHeightFromFloorAverage());
    }
    
    protected void interrupted() {
        end();
    }
}