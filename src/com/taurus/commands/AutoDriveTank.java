
package com.taurus.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

import com.taurus.robot.OI;
import com.taurus.robot.Robot;

public class AutoDriveTank extends Command {
    
    double speed;
    boolean tractionEnabled;
    
    /**
     * 
     * @param endTime seconds
     * @param speed -1 to 1
     * @param tractionEnabled 
     */
    public AutoDriveTank(double endTime, double speed, boolean tractionEnabled)
    {
        // Use requires() here to declare subsystem dependencies
        this.speed = speed;
        this.tractionEnabled = tractionEnabled;
        setTimeout(endTime);
        requires(Robot.rockerDriveSubsystem);
    }    

    // Called just before this Command runs the first time
    protected void initialize()
    {
        
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
        Robot.rockerDriveSubsystem.tankDrive(speed, speed, tractionEnabled);
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
