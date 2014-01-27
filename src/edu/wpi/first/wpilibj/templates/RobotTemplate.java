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
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.DigitalOutput; 
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot 
{
    ///////////////////////////////////////////////////////
    //  PRIVATE VARIABLES
    ///////////////////////////////////////////////////////
    /* Motor Objects */
    private RobotDrive chassis;
    private Victor grabberMotor;
    
    /* Left Joystick Setup */
    private Joystick leftStick;
    private final int shooterButton = 1;
    private final int grabberButton = 2;
    
    /* Right Joystick Setup */
    private Joystick rightStick;
    
    private boolean previousGrabberState = false;
    private boolean grabber = false;
    private final double grabberSpeed = 1.0;
    
    /* Shooter */
    private DigitalOutput tFiringArm;
    private DigitalOutput tLoadingPin;
    private DigitalInput sFiringArm;
    private DigitalInput sLoadingPin;
    private boolean firingArm = false;
    private boolean loadingPin = false;
    private boolean previousShooterState = false;
    private boolean firingReady = false;
    private final double timingDelay = 0.5;
    
    /* Camera */
    private AxisCamera camera;
    private final String cameraIP = "10.48.18.11";
    
    ///////////////////////////////////////////////////////
    //  PUBLIC METHODS
    ///////////////////////////////////////////////////////
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() 
    {
        /* Initialize Objects */
        chassis = new RobotDrive(1, 2);
        grabberMotor = new Victor(3);
        
        leftStick = new Joystick(1);
        rightStick = new Joystick(2);
        
        tFiringArm = new DigitalOutput(1);
        sFiringArm = new DigitalInput(2);
        tLoadingPin = new DigitalOutput(3);
        sLoadingPin = new DigitalInput(4);
        
        
        camera = AxisCamera.getInstance(cameraIP);
        
        // Inverting the Front left motor for driving
        chassis.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        // Retracting the shooter into position
        RetractShooter();
        // Add this in for 4WD
        //chassis.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);

    }
    
    /**
     * This function is called at the start of operator mode.
     */
    public void teleopInit()
    {
        chassis.setSafetyEnabled(true);
        chassis.tankDrive(leftStick, rightStick);
    }
    
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() 
    {
        chassis.tankDrive(leftStick, rightStick);
        GrabberMotorControls();
        ShooterControls();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() 
    {
        chassis.setSafetyEnabled(false);
        
        //TODO: Add code to shoot the ball
        
        // Drive forward out of the zone and stop
        chassis.drive(0.5, 0);
        Timer.delay(2.0);
        chassis.drive(0.0, 0.0);
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() 
    {
    
    }
    
    ///////////////////////////////////////////////////////
    //  PRIVATE METHODS
    ///////////////////////////////////////////////////////
    
    /**
     * DESCRIPTION: Operate the shooter when the shooter button
     *              is pressed.
     * ARGUMENTS:   None.
     */
    private void ShooterControls() 
    {
        if (leftStick.getRawButton(shooterButton))
        {
            if (firingReady)
            {
                fireShooter();
            }
            else
            {
                System.out.println("[!!] Not ready to fire. Aborting.");
            }
        }
    }

    /**
     * DESCRIPTION: Activate the controls for retracting the 
     *              shooter.
     * ARGUMENTS:   None.
     */
    private void reloadShooter() 
    {
        // Firing Stage 1
        if(!sFiringArm.get() && sLoadingPin.get()) //If robot has just fired
        {
            tLoadingPin.set(true);
            Timer.delay(timingDelay*5.0); //Need to fine tune this in case reloading takes longer
            // Firing Stage 2
            if(sFiringArm.get() && !sLoadingPin.get()) //If the arm is down but the loading mecahnism is still active
            {
                tFiringArm.set(false);
                Timer.delay(timingDelay);
                tLoadingPin.set(false);
                // Firing Stage 3
                if(sFiringArm.get() && sLoadingPin.get()) //If the robot is loaded and ready to go
                {
                    firingReady = true;
                }
                else
                {
                    firingReady = false;
                    System.out.println("[!!] Something went wrong in firing stage 3. Not ready to fire.");
                }
            }
            else
            {
                firingReady = false;
                System.out.println("[!!] Something went wrong in firing stage 2. Not ready to fire.");
            }
        }
        else
        {
            firingReady = false;
            System.out.println("[!!] Something went wrong in firing stage 1. Not ready to fire.");
        }
    }
    
    /**
     * DESCRIPTION: Activate the controls for shooting the 
     *              ball.
     * ARGUMENTS:   None.
     */
    private void fireShooter() 
    {
        System.out.println("[--] Firing!");
        firingReady = false;
        tFiringArm.set(true); //Release the firing arm
        Timer.delay(timingDelay); //Wait just a little bit
        reloadShooter();
    }

    /**
     * DESCRIPTION: Activates the grabber motor for grabbing
     *              a ball when the grabber button is pressed.
     * ARGUMENTS:   None.
     */
    private void GrabberMotorControls() 
    {
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
            grabberMotor.set(grabberSpeed);
        }
        else
        {
            grabberMotor.set(0);
        }
    }
}
