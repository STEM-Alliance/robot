package com.taurus.rocker;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class RockerChassis {

    CANTalon motorFR;
    CANTalon motorFL;
    CANTalon motorMR;
    CANTalon motorML;
    CANTalon motorBR;
    CANTalon motorBL;
    public RockerChassis(){
        motorFR = new CANTalon(10);
        motorFL = new CANTalon(11);
        motorMR = new CANTalon(12);
        motorML = new CANTalon(13);
        motorBR = new CANTalon(14);
        motorBL = new CANTalon(15);
//        motorFR.changeControlMode(TalonControlMode.Speed);
//        motorFR.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
//        motorFL.changeControlMode(TalonControlMode.Speed);
//        motorFL.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
//        motorMR.changeControlMode(TalonControlMode.Speed);
//        motorMR.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
//        motorML.changeControlMode(TalonControlMode.Speed);
//        motorML.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
//        motorBR.changeControlMode(TalonControlMode.Speed);
//        motorBR.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
//        motorBL.changeControlMode(TalonControlMode.Speed);
//        motorBL.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);

}
    /**
     * This is test code - drives the motors
     * @param right
     * @param left
     */
    public void tankDrive (double right, double left){
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
    public void tankDrive (double rightFront, double rightMid, double rightBack, double leftFront, double leftMid, double leftBack){
        motorBL.set(-leftBack);
        motorBR.set(rightBack);
        motorML.set(-leftMid);
        motorMR.set(rightMid);
        motorFL.set(-leftFront);
        motorFR.set(rightFront);
        
    }
    /**
     * Driving through arcade style drive
     * @param throttle
     * @param turn
     */
   public void arcadeDrive(double throttle, double turn){
       double left = -1;
       double right = 1;
       left = throttle + turn;
       right = throttle - turn;
       right = limit(right);
       left = limit(left);
       tankDrive(left, right);
   }
   /**
    * Limits value between 1 and -1
    * @param value
    * @return
    */
   private double limit(double value){
      double output = value;
       if(value > 1){
           output = 1;
       }
       else if(value < -1 ){
           output = -1;
       }
       return output;
   }
   
}
