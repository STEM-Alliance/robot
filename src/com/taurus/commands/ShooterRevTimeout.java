package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.OI;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class ShooterRevTimeout extends Command {
  
    private double timeout;
    private double startTime;

    public ShooterRevTimeout(double timeout) {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.shooterSubsystem);
        requires(Robot.ballReleaseSubsystem);
        this.timeout = timeout;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        // We want a constant fire speed, thus set the speed only once
        // If we have a vision, move this to execute and do calculation based on vision
        double speedTop = .65 + .2 * OI.getShooterSpeedAdjust();
        double speedBottom = .8 + .2 * OI.getShooterSpeedAdjust();
        Robot.shooterSubsystem.setSpeed(speedTop, speedBottom);
        
        startTime = Timer.getFPGATimestamp();
        
        //setTimeout(3);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Shooter", this);
        // We want a constant fire speed, thus set the speed only once
        // If we have a vision, move this to execute and do calculation based on vision
        double speedTop = .7 + .2 * OI.getShooterSpeedAdjust();
        double speedBottom = .8 + .2 * OI.getShooterSpeedAdjust();
        Robot.shooterSubsystem.setSpeed(speedTop, speedBottom);
        
        if (Timer.getFPGATimestamp() - startTime > timeout/2)
        {
            Robot.ballReleaseSubsystem.setAngle(45);
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Timer.getFPGATimestamp() - startTime > timeout;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
        Robot.shooterSubsystem.setSpeed(0,0);
    }
}
