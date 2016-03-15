
package com.taurus.commands;

import edu.wpi.first.wpilibj.command.Command;

import com.taurus.robot.Robot;

public class AutoDrive extends Command 
{
    
    final double speedR;
    final double speedL;
    boolean tractionEnabled;
    boolean gyroEnabled;
    
    /**
     * Default to not driving (speed equals zero)
     */
    public AutoDrive()
    {
        requires(Robot.rockerDriveSubsystem);
        speedR = 0;
        speedL = 0;
    }

    /**
     * Drive. Go any direction including turning.
     */
    public AutoDrive(double endTime, double speedL, double speedR, boolean tractionEnabled, boolean gryoEnabled)
    {
        requires(Robot.rockerDriveSubsystem);
        this.speedR = speedR;
        this.speedL = speedL;
        this.tractionEnabled = tractionEnabled;
        this.gyroEnabled = gyroEnabled;
        setTimeout(endTime);
    }
    
    public AutoDrive(double endTime, double speed, boolean tractionEnabled, boolean gyroEnabled)
    {
        this(endTime, speed, speed, tractionEnabled, gyroEnabled);
    }

    protected void initialize()
    {
        if(gyroEnabled)
        {   
            // Create our heading straight in front of our current position
            Robot.rockerDriveSubsystem.enableGyro(true);
            //Robot.rockerDriveSubsystem.zeroGyro(0); //TODO BDP Needed to zero here?
        }
    }

    protected void execute() 
    {
        Robot.rockerDriveSubsystem.driveRaw(speedR, speedL);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end() 
    {
        Robot.rockerDriveSubsystem.driveRaw(0, 0);
    }

    protected void interrupted()
    {
        
    }
}
