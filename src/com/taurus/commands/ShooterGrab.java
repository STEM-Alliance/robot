package com.taurus.commands;

import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ShooterGrab extends Command 
{    
    final double SHOOTER_SPEED = .6;
    
    public ShooterGrab() 
    {
        requires(Robot.shooterSubsystem);
        requires(Robot.ballReleaseSubsystem);
        requires(Robot.kickerSubsystem);
    }

    protected void initialize() 
    {
        
    }

    protected void execute() 
    {
        Robot.shooterSubsystem.setSpeed(-SHOOTER_SPEED, -SHOOTER_SPEED);
        Robot.kickerSubsystem.setSpeed(-1);
        Robot.ballReleaseSubsystem.setBallRelease(false);
    }

    protected boolean isFinished()
    {
        return false;//Robot.shooterSubsystem.stopSwitch.get();
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