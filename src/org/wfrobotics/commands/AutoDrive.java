
package org.wfrobotics.commands;

import org.wfrobotics.Vector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Targeting;

import edu.wpi.first.wpilibj.command.Command;

public class AutoDrive extends Command 
{
    enum MODE {DRIVE, ROTATE}
    
    public final static double SPEED_APPROACH = .5;
    final double speedR;
    final double speedL;
    double heading;
    double tolerance;
    boolean gyroEnabled;
    final MODE mode;  
    Vector vector = new Vector();

    
    /**
     * Default to not driving (speed equals zero)
     */
    public AutoDrive()
    {
        requires(Robot.driveSubsystem);
        this.mode = MODE.DRIVE;
        speedR = 0;
        speedL = 0;
    }

    /** 
     * Drive. Go any direction including turning.
     */
    public AutoDrive(double endTime, double speedL, double speedR, boolean gyroEnabled)
    {
        requires(Robot.driveSubsystem);
        this.mode = MODE.DRIVE;
        this.speedR = speedR;
        this.speedL = speedL;
        this.gyroEnabled = gyroEnabled;
        setTimeout(endTime);
    }
    
    public AutoDrive(double endTime, double speed, boolean gyroEnabled)
    {
        this(endTime, speed, speed, gyroEnabled);
    }
    
    public AutoDrive(double heading, double tolerance, AutoDrive.MODE mode)
    {
        requires(Robot.driveSubsystem);
        this.mode = mode;
        this.heading = heading; 
        this.tolerance = tolerance;
        this.speedL = 0;
        this.speedR = 0;       
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
        if(mode == MODE.DRIVE)
        {
            if(Robot.targetingSubsystem.DistanceToTarget() < Constants.OPTIMAL_SHOOTING_DISTANCE)
            {
                this.vector = new Vector(0, .3);
            }
            else if(Robot.targetingSubsystem.DistanceToTarget() > Constants.OPTIMAL_SHOOTING_DISTANCE)
            {
                this.vector= new Vector(0, -.3);
            }
            Robot.driveSubsystem.driveWithHeading(vector, 0, -1);
          //Robot.tankDriveSubsystem.driveRaw(speedR, speedL);    
        }
        else if(mode == MODE.ROTATE)
        {
          //TODO: allow us to choose the parameters
            Vector vector = new Vector(.5, .5); 
            Robot.driveSubsystem.driveWithHeading(vector, .5, heading);
        }
    }

    protected boolean isFinished()
    {
        if(mode == MODE.ROTATE)
        {
            boolean done = Robot.driveSubsystem.getLastHeading() - heading >= 0 - tolerance ||
                           Robot.driveSubsystem.getLastHeading() - heading <= 0 + tolerance;
            
            return done;
        }
        else
        {
            return isTimedOut();
        }
    }
    protected void end() 
    {
        //Robot.driveSubsystem.driveRaw(0, 0);
    }

    protected void interrupted()
    {
        
    }
}
