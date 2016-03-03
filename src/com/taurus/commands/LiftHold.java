package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class LiftHold extends Command
{
    double height;
    
    double startTime = 0;
    double initialBrakeWait = 1;
    double finalBrakeWait = 3;
    
    public LiftHold() {
        requires(Robot.liftSubsystem);
    }
    
    public LiftHold(double initialBrakeWait, double finalBrakeWait) {
        requires(Robot.liftSubsystem);
        
        this.initialBrakeWait = initialBrakeWait;
        this.finalBrakeWait = finalBrakeWait;
    }

    protected void initialize() {
        height = Robot.liftSubsystem.getHeightFromLiftBottomAverage();
        startTime = Timer.getFPGATimestamp();
    }

    protected void execute() {
        Utilities.PrintCommand("Lift", this);
        
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

    protected boolean isFinished() {
        return false;
    }

    protected void end() {
        Robot.liftSubsystem.enableBrakeMode(false);
    }

    protected void interrupted() {
        Robot.liftSubsystem.enableBrakeMode(false);
    }
}
