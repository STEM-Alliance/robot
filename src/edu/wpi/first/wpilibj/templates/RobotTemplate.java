/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;


import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import com.sun.squawk.debugger.Log;
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot 
{
        RobotDrive chassis = new RobotDrive(1, 2);
    Joystick leftStick = new Joystick(1);
    Joystick rightStick = new Joystick(2);
    boolean previousState = false;
    boolean grabber = false;
    Victor grabberMotor = new Victor(3);
    double grabberSpeed = 1;
    int grabberButton = 2;
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() 
    {
        Log.log("test");
        chassis.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        // Add this in for 4WD
        //chassis.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);

    }
    public void teleopInit()
    {
        chassis.setSafetyEnabled(true);
        chassis.tankDrive(leftStick, rightStick);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() 
    {
        chassis.setSafetyEnabled(false);
        chassis.drive(-0.5, 0);
        Timer.delay(2.0);
        chassis.drive(0.0, 0.0);

    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() 
    {
        chassis.tankDrive(leftStick, rightStick);
        if(leftStick.getRawButton(2)) 
        {
            System.out.println("test");
            if (previousState != true) 
            {
               grabber = !grabber; // toggle grabber motor
               previousState = true;
            }
        }
        else 
        {
           previousState = false;
        }
        if (grabber == true) 
        {
            grabberMotor.set((double)600.0/(double)6000.0);
        } 
        else 
        {
            grabberMotor.set(0);
        }
    }
       
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
