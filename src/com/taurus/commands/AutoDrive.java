
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
     * @param endTime
     * @param speedL
     * @param speedR
     * @param tractionEnabled
     * @param gryoEnabled
     */
    public AutoDrive(double endTime, double speedL, double speedR, boolean tractionEnabled, boolean gryoEnabled)
    {
        this.speedR = speedR;
        this.speedL = speedL;
        this.tractionEnabled = tractionEnabled;
        this.gyroEnabled = gyroEnabled;
        setTimeout(endTime);
        requires(Robot.rockerDriveSubsystem);
    }
    
    /** Drive. Goes straight forwards or backwards.
     * @param endTime seconds
     * @param speed -1 to 1
     * @param tractionEnabled 
     * @param gyroEnabled
     */
    public AutoDrive(double endTime, double speed, boolean tractionEnabled, boolean gyroEnabled)
    {
        this(endTime, speed, speed, tractionEnabled, gyroEnabled);
    }

    protected void initialize()
    {
        if(gyroEnabled)
        {   
            // Create our heading straight in front of our current position
            Robot.rockerDriveSubsystem.setGyroMode(true, true);
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

    // subsystems is scheduled to run
    protected void interrupted()
    {
        
    }
}
