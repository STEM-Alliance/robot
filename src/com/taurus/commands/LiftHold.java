package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.OI;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;

public class LiftHold extends Command
{
    double height;
    
    int dpadLast = -1;
    
    public LiftHold() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.liftSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        height = Robot.liftSubsystem.getHeightFromLiftBottomAverage();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Lift", this);
        
        int dpad = OI.getDpad2();
        
        if(dpadLast != dpad)
        {
            if(dpad == 0)
            {
                height = Preferences.getInstance().getDouble("LiftHighOffset", 40);
            }
            else if(dpad == 180)
            {
                height = Preferences.getInstance().getDouble("LiftLowOffset", 5);
            }
            else
            {
                height = Robot.liftSubsystem.getHeightFromLiftBottomAverage();
            }
            
            dpadLast = dpad;
        }
        
        Robot.liftSubsystem.setHeightFromLiftBottom(height);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
