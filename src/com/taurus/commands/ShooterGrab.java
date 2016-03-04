package com.taurus.commands;

import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ShooterGrab extends Command 
{    
    final double SHOOTER_SPEED = .4;
    
    public ShooterGrab() 
    {
        requires(Robot.shooterSubsystem);
        requires(Robot.ballReleaseSubsystem);
    }

    protected void initialize() 
    {
        
    }

    protected void execute() 
    {
        Robot.shooterSubsystem.setSpeed(-SHOOTER_SPEED, -SHOOTER_SPEED);
        Robot.ballReleaseSubsystem.setBallRelease(false);
    }

    protected boolean isFinished()
    {
        return Robot.shooterSubsystem.stopSwitch.get();
    }

    protected void end() 
    {
        Robot.shooterSubsystem.setSpeed(0,0);
    }

    protected void interrupted() 
    {
        end();
    }
}