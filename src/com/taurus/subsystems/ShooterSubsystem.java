package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.hardware.MagnetoPot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ShooterSubsystem extends Subsystem {
    
    private final double BALL_RELEASE_ANGLE_EXTENDED = 0;  // TODO - determine angle to go to extended position
    private final double BALL_RELEASE_ANGLE_CONTRACTED = 0;  // TODO - determine angle to go to contracted position
    
    public enum BALL_RELEASE_STATE {EXTENDED, CONTRACTED, MOVING};
    
    public DigitalInput stopSwitch;
    
    CANTalon shooterFT;
    CANTalon shooterFB;
    CANTalon shooterBT;
    CANTalon shooterBB;
    CANTalon aimer;
    PIDController aimerPID;
    MagnetoPot aimAngle;
    Servo ballRelease;
    double shooterStartTime;
    
    /**
     * Constructor
     */
    public ShooterSubsystem() {
        shooterFT = new CANTalon(20);
        shooterFB = new CANTalon(21);
        shooterBT = new CANTalon(22);
        shooterBB = new CANTalon(23);
        stopSwitch = new DigitalInput(0);
        aimer = new CANTalon(24);
        aimerPID = new PIDController(1, 0, 0, 1);
        aimAngle = new MagnetoPot(0,360);
        ballRelease = new Servo(0);  // TODO - change to correct pin
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
  
    /**]
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
    
    public BALL_RELEASE_STATE getBallRelease() {
        // TODO - Create deadband, cannot ensure we don't overshoot
        BALL_RELEASE_STATE result;
        
        if (ballRelease.getAngle() == BALL_RELEASE_ANGLE_CONTRACTED)
        {
            result = BALL_RELEASE_STATE.CONTRACTED;
        }
        else if (ballRelease.getAngle() == BALL_RELEASE_ANGLE_EXTENDED)
        {
            result = BALL_RELEASE_STATE.EXTENDED;
        }
        else
        {
            result = BALL_RELEASE_STATE.MOVING;
        }
        
        return result;
    }
    
    /**
     * aims the shooter
     * TODO - DRL We may want to create a Turret PIDSubsystem and Aim Command
     * @param angle 0 to 360
     * @return true if angle reached, false if not
     */
    public boolean aim(double angle){

        double motorOutput = aimerPID.update(angle, aimAngle.get());

        if(Math.abs(aimAngle.get()-angle) <= 5){
            aimer.set(0);
            return true;
        } else {
            aimer.set(motorOutput);
            return false;
        }
    }
}
