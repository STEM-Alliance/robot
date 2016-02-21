package com.taurus.subsystems;

import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ShooterSubsystem extends Subsystem 
{    
    private final double BALL_RELEASE_ANGLE_EXTENDED = 120;
    private final double BALL_RELEASE_ANGLE_CONTRACTED = 0;
    private final double TOLERANCE = 1;
    
    public DigitalInput stopSwitch;
    
    private CANTalon shooterFT;
    private CANTalon shooterFB;
    private Servo ballRelease;

    /**
     * Constructor
     */
    public ShooterSubsystem() 
    {
        shooterFT = new CANTalon(RobotMap.PIN_SHOOTER_TALON_FT);
        shooterFB = new CANTalon(RobotMap.PIN_SHOOTER_TALON_FB);
        
        shooterFT.setInverted(true);
        
        stopSwitch = new DigitalInput(RobotMap.PIN_SHOOTER_SENSOR_STOP);
        ballRelease = new Servo(RobotMap.PIN_SHOOTER_SERVO_BALLRELEASE);
    }
    
    public void initDefaultCommand() 
    {
        // Set the default command for a subsystem here.
    }    

    /**
     * Set outer wheel motor speed
     * @param topSpeed between -1 and 1
     * @param bottomSpeed between -1 and 1
     */
    public void setSpeed(double topSpeed, double bottomSpeed) 
    {
        shooterFT.set(topSpeed);
        shooterFB.set(bottomSpeed);
    }
  
    /**
     * Set the position of the ball releasing servo
     * @param extend If true out, otherwise in
     */
    public void setBallRelease(boolean extend) 
    {
        if (extend)
        {
            ballRelease.setAngle(BALL_RELEASE_ANGLE_EXTENDED);
        }
        else
        {
            ballRelease.setAngle(BALL_RELEASE_ANGLE_CONTRACTED);
        }
    }
    
    public boolean isBallReleaseExtended() 
    {
        return Math.abs(ballRelease.getAngle() - BALL_RELEASE_ANGLE_EXTENDED) < TOLERANCE;
    }
    
    public boolean isBallReleaseContracted() 
    {
        SmartDashboard.putNumber("ball release angle", ballRelease.getAngle());
        return Math.abs(ballRelease.getAngle() - BALL_RELEASE_ANGLE_CONTRACTED) < TOLERANCE;
    }
    
    public boolean isBallReleaseMoving() 
    {
        return !isBallReleaseExtended() && !isBallReleaseContracted();
    }
}
