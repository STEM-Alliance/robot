package com.taurus.commands;

import com.taurus.Utilities;
import com.taurus.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveToDistance extends Command{
    final double WHEEL_CIRCUMFERENCE = 20;  // inches    
    final double distanceDesired;
    
    double revsStarting;
    
    public DriveToDistance(double distance) 
    {
        requires(Robot.rockerDriveSubsystem);
        distanceDesired = distance;
        revsStarting = Robot.rockerDriveSubsystem.getEncoderRotations();
    }

    protected void initialize()
    {
        if (distanceDesired > 0)
        {
            Robot.rockerDriveSubsystem.driveRaw(1, 1);
        } 
        else if (distanceDesired < 0)
        {
            Robot.rockerDriveSubsystem.driveRaw(-1, -1);
        }    
    }
    
    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);
    }
    
    protected boolean isFinished() 
    {
        double distanceTraveled = 0;
        double rotations = Robot.rockerDriveSubsystem.getEncoderRotations();
        
        distanceTraveled = WHEEL_CIRCUMFERENCE * rotations;
        
        return Math.abs(distanceTraveled) >= Math.abs(distanceDesired);
    }
    
    protected void end() 
    {
        Robot.rockerDriveSubsystem.driveRaw(0, 0);
    }
    
    protected void interrupted()
    {
        
    }
}
