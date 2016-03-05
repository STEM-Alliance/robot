package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftHold extends Command
{
    double height;
    
    double startTime = 0;
    double initialBrakeWait = .251;
    double finalBrakeWait = 2;
    
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
                
        
        //SmartDashboard.putString("Lift Times", Timer.getFPGATimestamp() +" " + startTime +" " + initialBrakeWait +" " + finalBrakeWait);
        if((Timer.getFPGATimestamp() - startTime) > initialBrakeWait)
        {
            if(Robot.liftSubsystem.isLevel() || ((Timer.getFPGATimestamp() - startTime) > finalBrakeWait))
            {
                //SmartDashboard.putString("LiftSWBrake1", "true");
                Robot.liftSubsystem.enableBrakeMode(true);
                //Robot.liftSubsystem.setSpeed(0, 0);
            }
            else
            {
                //SmartDashboard.putString("LiftSWBrake1", "false");
                //Robot.liftSubsystem.setHeightFromLiftBottom(height);
            }
        }
        else
        {
            //SmartDashboard.putString("LiftSWBrake1", "false");
            //Robot.liftSubsystem.setHeightFromLiftBottom(height);
        }
        Robot.liftSubsystem.setHeightFromLiftBottom(height);

        //SmartDashboard.putString("LiftIsLevel1", Robot.liftSubsystem.isLevel() + "");
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
