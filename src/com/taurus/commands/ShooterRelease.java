package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.OI;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class ShooterRelease extends Command {
    
    double startTime = 0;
    public ShooterRelease() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.ballReleaseSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        Robot.ballReleaseSubsystem.setBallRelease(true);
        //setTimeout(2);
        startTime = Timer.getFPGATimestamp();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Shooter", this);
        // Once the ball is released, we should retract the servo to end in a safe state
        if (Timer.getFPGATimestamp() - startTime > 1.75)
        {
            Robot.ballReleaseSubsystem.setBallRelease(false);
        }
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Robot.ballReleaseSubsystem.isBallReleaseContracted() && (Timer.getFPGATimestamp() - startTime > 2.25);
    }
                
    // Called once after isFinished returns true
    protected void end() {
        //Robot.shooterSubsystem.setSpeed(0,0);
       
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        // Do not interrupt
    }
}
