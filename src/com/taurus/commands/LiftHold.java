package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.OI;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class LiftHold extends Command
{
    double height;
    
    double startTime = 0;
    double initialBrakeWait = 1;
    double finalBrakeWait = 3;
    
    int dpadLast = -1;

    public LiftHold() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.liftSubsystem);
    }
    
    public LiftHold(double initialBrakeWait, double finalBrakeWait) {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.liftSubsystem);
        
        this.initialBrakeWait = initialBrakeWait;
        this.finalBrakeWait = finalBrakeWait;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        height = Robot.liftSubsystem.getHeightFromLiftBottomAverage();
        startTime = Timer.getFPGATimestamp();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Lift", this);
        
//        int dpad = OI.getDpad2();
//        
//        if(dpadLast != dpad)
//        {
//            if(dpad == 0)
//            {
//                height = Preferences.getInstance().getDouble("LiftHighOffset", 40);
//            }
//            else if(dpad == 180)
//            {
//                height = Preferences.getInstance().getDouble("LiftLowOffset", 5);
//            }
//            else
//            {
//                height = Robot.liftSubsystem.getHeightFromLiftBottomAverage();
//            }
//            
//            dpadLast = dpad;
//        }
        
        
        if((Timer.getFPGATimestamp() - startTime) > initialBrakeWait)
        {
            if(Robot.liftSubsystem.isLevel() || ((Timer.getFPGATimestamp() - startTime) > finalBrakeWait))
            {
                Robot.liftSubsystem.enableBrakeMode(true);
                Robot.liftSubsystem.setSpeed(0, 0);
            }
        }
        else
        {
            Robot.liftSubsystem.setHeightFromLiftBottom(height);
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
        Robot.liftSubsystem.enableBrakeMode(false);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        Robot.liftSubsystem.enableBrakeMode(false);
    }
}
