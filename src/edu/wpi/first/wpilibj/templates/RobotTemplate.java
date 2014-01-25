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
import edu.wpi.first.wpilibj.DigitalOutput; 
/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot 
{
    /* Motor Objects */
    RobotDrive chassis = new RobotDrive(1, 2);
    Victor grabberMotor = new Victor(3);
    
    /* Left Stick Setup */
    Joystick leftStick = new Joystick(1);
    final int shooterButton = 1;
    final int grabberButton = 2;
    
    /* Right Stick Setup */
    Joystick rightStick = new Joystick(2);
    
    boolean previousGrabberState = false;
    boolean grabber = false;
    double grabberSpeed = 1;
    
    /* Shooter */
    DigitalOutput shooterOn = new DigitalOutput(1);
    DigitalOutput shooterOff = new DigitalOutput(2);
    boolean previousShooterState = false;
            
    
    
    
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() 
    {
        Log.log("test");
        chassis.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        shooterOn.set(false);
        shooterOff.set(true);
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
        if(leftStick.getRawButton(grabberButton)) 
        {
            System.out.println("test");
            if (previousGrabberState != true) 
            {
               grabber = !grabber; // toggle grabber motor
               previousGrabberState = true;
            }
        }
        else 
        {
           previousGrabberState = false;
        }
        if (grabber == true) 
        {
            grabberMotor.set(1.0);
        } 
        else 
        {
            grabberMotor.set(0);
        }
        if (leftStick.getRawButton(shooterButton))
        {
            if (previousShooterState != true) 
            {
               previousShooterState = true;
               shooterOff.set(false);
               shooterOn.set(true);
               Timer.delay(2.0);
               shooterOn.set(false);
               shooterOff.set(true);
            }
        }
        else 
        {
           previousShooterState = false;
        }
    }
       
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
}
