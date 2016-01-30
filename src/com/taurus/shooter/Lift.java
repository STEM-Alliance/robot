package com.taurus.shooter;

import com.taurus.PIDController;
import com.taurus.hardware.MagnetoPot;

import edu.wpi.first.wpilibj.CANTalon;

public class Lift {
    CANTalon motorLeft;
    CANTalon motorRight;
    MagnetoPot potLeft;
    MagnetoPot potRight;
    PIDController heightPID;
    private double upperLimit = 1200;
    private double lowerLimit = 325;
    private double currentHeight = (Math.sin(potLeft.get())*711)*2;
    /**
     * Constructor
     */
    public Lift(){
        motorLeft = new CANTalon(30);
        motorRight = new CANTalon(31);
        potLeft = new MagnetoPot(0,360);
        potRight = new MagnetoPot(0,360);
        heightPID = new PIDController(1, 0, 0, 1);
    }
    public double getCurrentHeight(){
        return currentHeight;
    }
    
    /**
     * set lift height
     * @param height we want to be at in millimeters
     * @return whether at desired height or not: will be a true/false and move accordingly
     */
    public boolean setHeight(double height) {
       
        double motorOutput = heightPID.update(height, currentHeight);
        if(height >= upperLimit){
            height = upperLimit;
        } else if(height <= lowerLimit){
            height = lowerLimit;
        } else {
            //within limits
        }
        if(Math.abs(currentHeight-height) <= 20){
           setMotorSpeed(0);
            return true;
        } else {
            setMotorSpeed(motorOutput);
            return false;
        }
    }
    
    /**
     * helper function: sets the motor speeds for the lift to the same value
     * @param speed between 0 to 1
     */
    private void setMotorSpeed(double speed){
        motorRight.set(speed);
        motorLeft.set(speed);
        
    }
    public void stopLift(){
        setMotorSpeed(0);
    }
 
}
