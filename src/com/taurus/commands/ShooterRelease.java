package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

public class ShooterRelease extends Command 
{    
    double startTime = 0;
    
    public ShooterRelease()
    {
        requires(Robot.ballReleaseSubsystem);
    }

    protected void initialize()
    {
        Robot.ballReleaseSubsystem.setBallRelease(true);
        //setTimeout(2);
        startTime = Timer.getFPGATimestamp();
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Shooter", this);
        Robot.ballReleaseSubsystem.setBallRelease(true);
        // Once the ball is released, we should retract the servo to end in a safe state
        if (Timer.getFPGATimestamp() - startTime > 1.75)
        {
            Robot.ballReleaseSubsystem.setBallRelease(false);
        }
    }

    protected boolean isFinished() 
    {
        return Robot.ballReleaseSubsystem.isBallReleaseContracted() && (Timer.getFPGATimestamp() - startTime > 2.25);
    }
    
    protected void end() 
    {
        Robot.shooterSubsystem.setSpeed(0,0);
    }

    protected void interrupted() 
    {
        // Do not interrupt
    }
}
