
package com.taurus.commands;

import edu.wpi.first.wpilibj.command.Command;

import com.taurus.robot.Robot;

public class AutoDrive extends Command 
{
    /**
     * Used to translate a state (such as starting position on the field) into descriptor of how to move
     */
    public enum STATE_TURN
    {
        // TODO - DRL can we run at speed magnitude of 1? If not, what is the cap? Let's use that?
        POSITION_ONE(1, -.5, .5),     // TODO - DRL try (.66, -.75, .75) or faster
        POSITION_TWO(.5, -.5, .5),    // TODO - DRL Determine ideal values
        POSITION_THREE(0, 0, 0),      // TODO - DRL Determine ideal values
        POSITION_FOUR(.4, .5, -.5),   // TODO - DRL Determine ideal values
        POSITION_FIVE(.8, .5, -.5),;  // TODO - DRL Determine ideal values

        private final double time, speedR, speedL;
        
        private STATE_TURN(double time, double speedR, double speedL) 
        {
            this.time = time;
            this.speedR = speedR;
            this.speedL = speedL;
        }
        
        public double getTime()
        {
            return time;
        }

        public double getSpeedR() 
        {
            return speedR;
        }
        
        public double getSpeedL() 
        {
            return speedL;
        }
    }
    
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
    
    public AutoDrive(STATE_TURN turnBasedOnPosition)
    {
        requires(Robot.rockerDriveSubsystem);
        this.speedR = turnBasedOnPosition.getSpeedR();
        this.speedL = turnBasedOnPosition.getSpeedL();
        setTimeout(turnBasedOnPosition.getTime());
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

    protected void interrupted()
    {
        
    }
}
