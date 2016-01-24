package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.hardware.MagnetoPot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ShooterSubsystem extends Subsystem {
    
    public DigitalInput stopSwitch;
    
    CANTalon shooterFT;
    CANTalon shooterFB;
    CANTalon shooterBT;
    CANTalon shooterBB;
    CANTalon aimer;
    PIDController aimerPID;
    MagnetoPot aimAngle;
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
