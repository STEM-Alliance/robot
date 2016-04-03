
package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class AutoDrive extends Command 
{
    public final static double SPEED_APPROACH = .5;
    final double speedR;
    final double speedL;
    boolean gyroEnabled;
    
    /**
     * Default to not driving (speed equals zero)
     */
    public AutoDrive()
    {
        requires(Robot.tankDriveSubsystem);
        speedR = 0;
        speedL = 0;
    }

    /**
     * Drive. Go any direction including turning.
     */
    public AutoDrive(double endTime, double speedL, double speedR, boolean gyroEnabled)
    {
        requires(Robot.tankDriveSubsystem);
        this.speedR = speedR;
        this.speedL = speedL;
        this.gyroEnabled = gyroEnabled;
        setTimeout(endTime);
    }
    
    public AutoDrive(double endTime, double speed, boolean gyroEnabled)
    {
        this(endTime, speed, speed, gyroEnabled);
    }

    protected void initialize()
    {
        if(gyroEnabled)
        {   
            // Create our heading straight in front of our current position
            //Robot.rockerDriveSubsystem.zeroGyro(0); //TODO BDP Needed to zero here?
        }
    }

    protected void execute() 
    {
        Robot.tankDriveSubsystem.driveRaw(speedR, speedL);
    }

    protected boolean isFinished()
    {
        return isTimedOut();
    }

    protected void end() 
    {
        Robot.tankDriveSubsystem.driveRaw(0, 0);
    }

    protected void interrupted()
    {
        
    }
}
