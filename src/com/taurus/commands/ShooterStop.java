package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class ShooterStop extends Command
{    
    public ShooterStop() 
    {
        requires(Robot.shooterSubsystem);
    }

    protected void initialize() 
    {
        
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Shooter", this);
        Robot.shooterSubsystem.setSpeed(0,0);
    }

    protected boolean isFinished() 
    {
        return true;
    }

    protected void end() 
    {
        
    }

    protected void interrupted() 
    {
        end();
    }
}