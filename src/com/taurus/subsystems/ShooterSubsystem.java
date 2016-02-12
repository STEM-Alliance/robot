package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.hardware.MagnetoPot;
import com.taurus.robot.RobotMap;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ShooterSubsystem extends Subsystem {
    
    private final double BALL_RELEASE_ANGLE_EXTENDED = 0;  // TODO - determine angle to go to extended position
    private final double BALL_RELEASE_ANGLE_CONTRACTED = 0;  // TODO - determine angle to go to contracted position
    
    public DigitalInput stopSwitch;
    
    private CANTalon shooterFT;
    private CANTalon shooterFB;
    private CANTalon shooterBT;  // TODO - DRL remove if design changes and is unused
    private CANTalon shooterBB;  // TODO - DRL remove if design changes and is unused
    private Servo ballRelease;

    
    
    /**
     * Constructor
     */
    public ShooterSubsystem() {
        shooterFT = new CANTalon(RobotMap.PIN_SHOOTER_TALON_FT);
        shooterFB = new CANTalon(RobotMap.PIN_SHOOTER_TALON_FB);
        shooterBT = new CANTalon(RobotMap.PIN_SHOOTER_TALON_BT);
        shooterBB = new CANTalon(RobotMap.PIN_SHOOTER_TALON_BB);
        stopSwitch = new DigitalInput(RobotMap.PIN_SHOOTER_SENSOR_STOP);
        ballRelease = new Servo(RobotMap.PIN_SHOOTER_SERVO_BALLRELEASE);
    }
    
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
    }    

    /**
     * Set outer wheel motor speed
     * @param topSpeed between -1 and 1
     * @param bottomSpeed between -1 and 1
     */
    public void setSpeed(double topSpeed, double bottomSpeed) {
        shooterFT.set(topSpeed);
        shooterFB.set(-bottomSpeed);
    }
  
    /**
     * Set the position of the ball releasing servo
     * @param extend If true out, otherwise in
     */
    public void setBallRelease(boolean extend) {
        if (extend)
        {
            ballRelease.setAngle(BALL_RELEASE_ANGLE_EXTENDED);
        }
        else
        {
            ballRelease.setAngle(BALL_RELEASE_ANGLE_CONTRACTED);
        }
    }
    
    public boolean isBallReleaseExtended() {
        // TODO - DRL Create deadband, cannot ensure we don't overshoot
        return ballRelease.getAngle() == BALL_RELEASE_ANGLE_EXTENDED;
    }
    
    public boolean isBallReleaseContracted() {
        // TODO - DRL Create deadband, cannot ensure we don't overshoot
        return ballRelease.getAngle() == BALL_RELEASE_ANGLE_CONTRACTED;
    }
    
    public boolean isBallReleaseMoving() {
        return !isBallReleaseExtended() && !isBallReleaseContracted();
    }
    

}
