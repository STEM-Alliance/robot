package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveToDistance extends Command{
    public final double WHEEL_CIRCUMFERENCE = 20;  // inches
    
    double distanceDesired;
    double revsStarting;
    
    public DriveToDistance(double distance) {  
        // Use requires() here to declare subsystem dependencies
        requires(Robot.rockerDriveSubsystem);
        distanceDesired = distance;
        revsStarting = Robot.rockerDriveSubsystem.getEncoderRotations();
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        if (distanceDesired > 0)
        {
            Robot.rockerDriveSubsystem.tankDrive(1, 1, true);
        } 
        else if (distanceDesired < 0)
        {
            Robot.rockerDriveSubsystem.tankDrive(-1, -1, true);
        }    
    }
    
    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        Utilities.PrintCommand("Drive", this);
    }
    
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        double distanceTraveled = 0;
        double rotations = Robot.rockerDriveSubsystem.getEncoderRotations();
        
        distanceTraveled = WHEEL_CIRCUMFERENCE * rotations;
        
        return Math.abs(distanceTraveled) >= Math.abs(distanceDesired);
    }
    
    // Called once after isFinished returns true
    protected void end() {
        Robot.rockerDriveSubsystem.tankDrive(0, 0, false);
    }
    
    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
