package org.wfrobotics.robot.subsystems;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;

public class CameraServos extends Subsystem {
    
/*
 *      -----The Vision TestBoard Project of summer 2k17!-----
 *  Goal: Make the vision on the heard not total potato! ( Or just a start )
 *  
 *      Stage 1) Make this class be able to control the 'X' ( pan / yaw ) and 'Y' ( tilt / pitch ) axises with servos
 * Wishlist?         1.1) Add Pot. sensors to detect the angle of the servo
 * Lee?         1.2) Model / make a better mounting solution for the camera and the servos 
 * Complete:         1.2) Enable it to be controlled by Xbox controller's joystick to be translated into a position on the testboard
 *          
 *      Stage 2) Connect the stage 1 classes to the network table over network tables from the Kangaroo running GRIP
 *          2.1) Create a pitch control in the Python GRIP library
 *                  2.1.1) Required changes to the distance calculations to take in account for the new angle of the camera?
 *          2.2) Make the connection take frames -> process them -> Give them to the RIO -> make accurate movement
 *                  2.2.3) Take 1 frame at a time move to target { record latency }
 *                  2.2.4) Try taking frames while moving to see if motion blur effects the system { record Latency }
 *                  2.2.5) Compare results to determine if we need to stop to take frames
 *         2.3) switch out the servos with motors and SRX's
 *                  2.3.1) Tune the PID loops for fast motor movement
 *      Stage 3) Tune the PID loops more efficiently Reduce Latency ** tuning shouldn't be done 10 min. before a match! **
 *         3.1) switch the network table to a TCP or UDP server / client for faster communications between the RIO and the Kangaroo
 *         3.2) hook system up to a different subsystem 
 *                  3.2.1) Make the drive subsystem reactive based on the system      
 *         
 * Wish list / ideas:
 *          -Change the Net Table to a TCP / UDP server transactions to improve speed
 *          -Very low latency system
 *          -Light sensor to dynamically adjust the aperture and exposure of the camera in different arena conditions
 *          -Use more calculations done on the Kangaroo to take load off of the RIO
 *          -Smarter LED turning on and off
 *          -multiply camera / vision support with the Kangaroo (Windows / Grip had problems with the default camera)
 * 
 * 
 * ************** Troy in not responsible in any way for widespread spelling errors.... ***************
 *                  Tip: If you can't read it sound out the word literally out loud!
 */
    
    Servo servoX,
          servoY;
    
    public enum State { searching, found, locked, off };
        
    public State currentState;
           
    boolean hasTarget = false;
    
    public CameraServos()
    {
        servoX = new Servo(7);
        servoY = new Servo(6);
        
    }
    
    @Override
    protected void initDefaultCommand()
    {
        //setDefaultCommand(new VisionDetect(Robot.camServoSubsystem, MODE.GETDATA));
        //setDefaultCommand(new VisionTracking(Robot.camServoSubsystem));
    }
    /**
     * Sets servo from center as:
     *      val > 0.5 moves right 
     *      val < 0.5 moves left
     *                  
     * @param val double 0 - 1
     */
    public void setX(double val)
    {
        servoX.set(val + 0.5);
    }
    /**
     * Sets servo from center as:
     *      val > 0.5 moves right 
     *      val < 0.5 moves left
     *                  
     * @param val double 0 - 1
     */
    public void setY(double val)
    {
            servoY.set(val + 0.5);
    }

}
