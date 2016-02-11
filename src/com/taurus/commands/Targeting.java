package com.taurus.commands;

import com.taurus.robot.OI;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class Targeting extends Command {
    
    private boolean shooterAimed;
    private boolean driveAimed;
    
    
    public Targeting() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.shooterSubsystem);
        requires(Robot.rockerDriveSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        shooterAimed = false;
        driveAimed = false;
        
        //setTimeout(1);
        
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        
        shooterAimed = Robot.shooterSubsystem.aim();
        driveAimed = Robot.rockerDriveSubsystem.aim();
        
        
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return shooterAimed && driveAimed;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        end();
    }
}
