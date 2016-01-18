package com.taurus.shooter;

import com.taurus.MagnetoPot;
import com.taurus.PIDController;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

public class Shooter {

    CANTalon shooterFT = new CANTalon(20);
    CANTalon shooterFB = new CANTalon(21);
    CANTalon shooterBT = new CANTalon(22);
    CANTalon shooterBB = new CANTalon(23);
    DigitalInput stopSwitch = new DigitalInput(0);
    CANTalon aimer = new CANTalon(24);
    PIDController aimerPID = new PIDController(1, 0, 0, 1);
    MagnetoPot aimAngle = new MagnetoPot(0,360);
    /**
     * grabs the boulder - top and bottom opposite directions
     * @param speed between 0 and 1
     */
    public void grab (double speed){
      
      if (stopSwitch.get() == false){
          setSpeed(speed, -speed);
      }
      else {
          stop();
      }
    }
    
    /**
     * stops all motors to hold boulder
     */
    public void stop (){
        setSpeed(0,0);
        
    }
    
    /**
     * shoots the ball 
     * @param speed between 0 to 1
     */
    public void shoot (double speed){
        setSpeed(-speed, speed);
    
    }
    /**
     * aims the shooter
     * @param angle 0 to 360
     * @return true if angle reached, false if not
     */
    
    public boolean aim(double angle){
        
       double motorOutput = aimerPID.update(angle, aimAngle.get(), Timer.getFPGATimestamp());
       
       if(Math.abs(aimAngle.get()-angle) <= 5){
           aimer.set(0);
           return true;
       } else {
           aimer.set(motorOutput);
           return false;
       }
       
    }
    
    /**
     * helper function to set speeds 
     * @param topSpeed between -1 and 1
     * @param bottomSpeed between -1 and 1
     */
    private void setSpeed (double topSpeed, double bottomSpeed){
        shooterFT.set(topSpeed);
        shooterFB.set(bottomSpeed);
        shooterBT.set(topSpeed);
        shooterBB.set(bottomSpeed);
    }
}
