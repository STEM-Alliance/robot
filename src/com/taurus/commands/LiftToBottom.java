package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class LiftToBottom extends Command {
    private boolean done;
    
    public LiftToBottom() {
        requires(Robot.liftSubsystem);
    }
    
    protected void initialize() {
        done = false;
    }
    
    protected void execute() {
        Utilities.PrintCommand("Lift", this);
        double endHeight = Robot.liftSubsystem.LIMIT_LOWER + Preferences.getInstance().getDouble("LiftLowOffset", 5);
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