package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class LiftContinuous extends Command {
    
    boolean raise = false;
    
    public LiftContinuous(boolean raise) {
        requires(Robot.liftSubsystem);
        this.raise = raise;
    }
    
    protected void initialize() {
    }
    
    protected void execute() {
        Utilities.PrintCommand("Lift", this);
        
        if(raise)
        {
            Robot.liftSubsystem.setSpeed(.5, .5);
        }
        else
        {
            Robot.liftSubsystem.setSpeed(-.5, -.5);
        }
    }

    protected boolean isFinished() {
        return false;
    }
    
    protected void end() {
        Robot.liftSubsystem.setSpeed(0, 0);
    }
    
    protected void interrupted()
    {
        end();
    }
}