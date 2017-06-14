package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.commands.vision.XboxGimbal;
import org.wfrobotics.reuse.subsystems.NetworkTableCamera;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CameraServos extends NetworkTableCamera {
    
/*
 *      -----The Vision TestBoard Project of summer 2k17!-----
 *  Goal: Make the vision on the heard not total potato! ( Or just a start )
 *  
 *      Stage 1) Make this class be able to control the 'X' ( pan / yaw ) and 'Y' ( tilt / pitch ) axises with servos
 *          1.1) Add Pot. sensors to detect the angle of the servo
 *          1.2) Model / make a better mounting solution for the camera and the servos 
 *          1.2) Enable it to be controlled by Xbox controller's joystick to be translated into a position on the testboard
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
 *          -Change the 
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
       
    public CameraServos()
    {
        super("Target", 0, 1);
        
        servoX = new Servo(8);
        servoY = new Servo(9);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new XboxGimbal());
    }

    @Override
    public void run()
    {
        
//        getUpdatedData();
        
//        TargetCount = data.size();
//        SmartDashboard.putNumber("GearTargets", TargetCount);
        
        if(TargetCount == 1)
        {
//            TargetData validData = data.get(0);
            //validData.
            
        }
//        servoX.set(xCalc());
//        xCalc();
//        yCalc();
        
    }
    public double xCalc()
    {
        
        
        return 0.0;  
    }
    public double yCalc()
    {
        
        
        return 0.0;
    }
    
    public void setX(double val)
    {
        // the Xbox Joystick is -90-90 however the servo movement is 0-180; therefore 90 degree offset?
        servoX.set(val + 90);
    }
    public void setY(double val)
    {
        // the Xbox Joystick is -90-90 however the servo movement is 0-180; therefore 90 degree offset?
            servoY.set(val + 90);
    }

}
