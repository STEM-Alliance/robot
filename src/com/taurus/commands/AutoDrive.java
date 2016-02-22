
package com.taurus.commands;

import edu.wpi.first.wpilibj.command.Command;

import com.taurus.robot.Robot;

public class AutoDrive extends Command {
    
    double speed;
    boolean tractionEnabled;
    boolean gyroEnabled;
    
    /**
     * 
     * @param endTime seconds
     * @param speed -1 to 1
     * @param tractionEnabled 
     * @param gyroEnabled
     */
    public AutoDrive(double endTime, double speed, boolean tractionEnabled, boolean gyroEnabled)
    {
        // Use requires() here to declare subsystem dependencies
        this.speed = speed;
        this.tractionEnabled = tractionEnabled;
        this.gyroEnabled = gyroEnabled;
        setTimeout(endTime);
        requires(Robot.rockerDriveSubsystem);
    }    

    // Called just before this Command runs the first time
    protected void initialize()
    {
        if(gyroEnabled)
        {   
            // Create our heading straight in front of our current position
            Robot.rockerDriveSubsystem.setGyroMode(true, true);
        }
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
        Robot.rockerDriveSubsystem.driveRaw(speed, speed, tractionEnabled);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return isTimedOut();
    }

    // Called once after isFinished returns true
    protected void end() 
    {
        
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
        
    }
}
