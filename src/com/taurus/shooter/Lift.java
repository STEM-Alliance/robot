package com.taurus.shooter;

import com.taurus.MagnetoPot;
import com.taurus.PIDController;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Timer;

public class Lift {
    CANTalon motorLeft = new CANTalon(30);
    CANTalon motorRight = new CANTalon(31);
    MagnetoPot potLeft = new MagnetoPot(0,360);
    MagnetoPot potRight = new MagnetoPot(0,360);
    PIDController heightPID = new PIDController(1, 0, 0, 1);
    
    
    /**
     * set lift height
     * @param height we want to be at
     * @return whether at desired height or not: will be a true/false and move accordingly
     */
    public boolean setHeight(double height) {
        double currentHeight = (Math.sin(potLeft.get())*711)*2;
        double motorOutput = heightPID.update(height, currentHeight, Timer.getFPGATimestamp());
        
        if(Math.abs(currentHeight-height) <= 20){
           setMotorSpeed(0);
            return true;
        } else {
            setMotorSpeed(motorOutput);
            return false;
        }
    }
    
    /**
     * sets the motor speeds for the lift to the same value
     * @param speed between 0 to 1
     */
    private void setMotorSpeed(double speed){
        motorRight.set(speed);
        motorLeft.set(speed);
        
    }
 
}
