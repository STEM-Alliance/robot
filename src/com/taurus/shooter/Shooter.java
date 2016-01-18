package com.taurus.shooter;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;

public class Shooter {

    CANTalon shooterFT = new CANTalon(20);
    CANTalon shooterFB = new CANTalon(21);
    CANTalon shooterBT = new CANTalon(22);
    CANTalon shooterBB = new CANTalon(23);
    DigitalInput stopSwitch = new DigitalInput(0);
    
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
