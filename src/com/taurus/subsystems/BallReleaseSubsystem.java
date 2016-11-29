package com.taurus.subsystems;

import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class BallReleaseSubsystem extends Subsystem 
{    
    private final double BALL_RELEASE_ANGLE_EXTENDED = 145;
    private final double BALL_RELEASE_ANGLE_CONTRACTED = 0;
    private final double TOLERANCE = 1;
    
    private Servo ballRelease1;
    private Servo ballRelease2;
   
    /**
     * Constructor
     */
    public BallReleaseSubsystem() 
    {
      
        ballRelease1 = new Servo(RobotMap.PIN_SERVO_SHOOTER_BALL_RELEASE_1);
        ballRelease2 = new Servo(RobotMap.PIN_SERVO_SHOOTER_BALL_RELEASE_2);
    }
    
    public void initDefaultCommand() 
    {
        // Set the default command for a subsystem here.
    }    

    /**
     * Set the position of the ball releasing servo
     * @param extend If true out, otherwise in
     */
    public void setBallRelease(boolean extend) 
    {
        if (extend)
        {
            ballRelease1.setAngle(BALL_RELEASE_ANGLE_EXTENDED);
            ballRelease2.setAngle(BALL_RELEASE_ANGLE_CONTRACTED);
        }
        else
        {
            ballRelease1.setAngle(BALL_RELEASE_ANGLE_CONTRACTED);
            ballRelease2.setAngle(BALL_RELEASE_ANGLE_EXTENDED);
        }
    }
    
    public void setAngle(double angle)
    {
        ballRelease1.setAngle(angle);
        ballRelease2.setAngle(angle);
    }
    
    public boolean isBallReleaseExtended() 
    {
        return Math.abs(ballRelease1.getAngle() - BALL_RELEASE_ANGLE_EXTENDED) < TOLERANCE;
    }
    
    public boolean isBallReleaseContracted() 
    {
        SmartDashboard.putNumber("ball release angle", ballRelease1.getAngle());
        return Math.abs(ballRelease1.getAngle() - BALL_RELEASE_ANGLE_CONTRACTED) < TOLERANCE;
    }
    
    public boolean isBallReleaseMoving() 
    {
        return !isBallReleaseExtended() && !isBallReleaseContracted();
    } 
}
