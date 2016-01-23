package com.taurus.rocker;

import edu.wpi.first.wpilibj.CANTalon;

public class RockerChassis {

    CANTalon motorFR = new CANTalon(10);
    CANTalon motorFL = new CANTalon(11);
    CANTalon motorMR = new CANTalon(12);
    CANTalon motorML = new CANTalon(13);
    CANTalon motorBR = new CANTalon(14);
    CANTalon motorBL = new CANTalon(15);
    
    /**
     * This is test code - drives the motors
     * @param right
     * @param left
     */
    public void drive (double right, double left){
        motorBL.set(-left);
        motorBR.set(right);
        motorML.set(-left);
        motorMR.set(right);
        motorFL.set(-left);
        motorFR.set(right);
        
    }

    /**
     * This is test code - drives the motors
     * @param right
     * @param left
     */
    public void drive (double rightFront, double rightMid, double rightBack, double leftFront, double leftMid, double leftBack){
        motorBL.set(-leftBack);
        motorBR.set(rightBack);
        motorML.set(-leftMid);
        motorMR.set(rightMid);
        motorFL.set(-leftFront);
        motorFR.set(rightFront);
        
    }
}
