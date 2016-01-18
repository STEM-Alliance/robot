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
    
    
    
    public boolean setHeight(double height) {
        double currentHeight = (Math.sin(potLeft.get())*711)*2;
        double motorOutput = heightPID.update(height, currentHeight, Timer.getFPGATimestamp());
        
        if(Math.abs(currentHeight-height) <= 20){
            motorLeft.set(0);
            motorRight.set(0);
            return true;
        } else {
            motorLeft.set(motorOutput);
            motorRight.set(motorOutput);
            return false;
        }
    }
    
    private void set(double speed){
        motorRight.set()
    }
 
}
