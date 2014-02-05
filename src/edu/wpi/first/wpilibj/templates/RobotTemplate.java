/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;


import com.sun.cldc.jna.Structure;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.DigitalOutput; 
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor; 


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
    
    // solenoid //
    public Solenoid tFiringArmOut;
    public Solenoid tFiringArmIn;
    public Solenoid tLoadingPinIn;
    public Solenoid tLoadingPinOut;
    public Solenoid armOut;
    public Solenoid armIn;
    
    public AnalogChannel cAnalogTest;
    
    /* Left Joystick Setup */
    private Joystick leftStick;
    private final int shooterButton = 1;
    private final int grabberButton = 2;
    private final int armControlButton = 3;
    //private final int compressorButton = 11;
    
    /* Right Joystick Setup */
    private Joystick rightStick;
    
    private boolean previousGrabberState = false;
    private boolean grabber = false;
    private final double grabberSpeed = 1.0;
    private boolean previousArmState = false;
    private boolean previousArmButtonState = false;
    
    
    
    /* Shooter */
    
    
    private DigitalInput sFiringArm;
    private DigitalInput sLoadingPin;
    private Relay tCompressor;
    private boolean firingArm = false;
    private boolean loadingPin = false;
    private boolean previousCompressorState = false;
    private boolean firingReady = false;
    private final double timingDelay = 0.5;
    private Compressor compressor;
    
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
        chassis = new RobotDrive(1, 2, 3, 4);
        grabberMotor = new Victor(5);
        
        leftStick = new Joystick(1);
        rightStick = new Joystick(2);
        
        //    Firing arm
        sFiringArm = new DigitalInput(3);
        sLoadingPin = new DigitalInput(2);
        tFiringArmIn  = new Solenoid(1);
        tFiringArmOut = new Solenoid(2);
        tLoadingPinIn = new Solenoid(3);
        tLoadingPinOut  = new Solenoid(4);
        armOut = new Solenoid(5);
        armIn = new Solenoid(6);
       
      
        compressor = new Compressor(1,1);             
        
        //camera = AxisCamera.getInstance(cameraIP);
        
        // Inverting the Front left motor for driving
        //chassis.setInvertedMotor(RobotDrive.MotorType.kFrontLeft, true);
        // Retracting the shooter into position
        //RetractShooter(); Later implementation
        // Add this in for 4WD
        chassis.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);

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
        compressorControl();
        ShooterStateMachine();
        GrabberMotorControls();
       
       
        
       
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() 
    {
        chassis.setSafetyEnabled(false);
        
        
        
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
    
    
    private final int ShooterStateStart =                   0;
    private final int ShooterStateRetractFiringPin =        1;
    private final int ShooterStateRetractFiringPinWait =    2;
    private final int ShooterStateSetFiringArm =            3;
    private final int ShooterStateSetFiringArmWait =        4;
    private final int ShooterStateSetFiringPin =            5;
    private final int ShooterStateSetFiringPinWait =        6;
    private final int ShooterStateRetractFiringMech =       7;
    private final int ShooterStateRetractFiringMechWait =   8;
    private final int ShooterStateFireReady =               9;
    private final int ShooterStateFireWait =                10;
    
    private int currentReloadShooterState = ShooterStateStart;
    private int newReloadShooterState = ShooterStateStart;
    
    private double shooterTime = 0;
    
    private final int FIRING_ARM_WAIT = 10;
    private final int LOADING_PIN_WAIT = 2;
    private final int FIRING_WAIT = 2;
    
    /**
     * DESCRIPTION: Activate the controls for the 
     *              shooter.
     * ARGUMENTS:   None.
     */
    private void ShooterStateMachine()
    {
        switch(currentReloadShooterState)
        {
            case ShooterStateStart:
            {
                System.out.println("reloadShooterStateMachine: ShooterStateStart");
                newReloadShooterState = ShooterStateRetractFiringPin;
                break;
            }
            case ShooterStateRetractFiringPin:
            {
                System.out.println("reloadShooterStateMachine: ShooterStateRetractFiringPin");
                tLoadingPinIn.set(false);
                tLoadingPinOut.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newReloadShooterState = ShooterStateRetractFiringPinWait;
                break;
            }
            case ShooterStateRetractFiringPinWait:
            {
                System.out.println("reloadShooterStateMachine: ShooterStateRetractFiringPinWait");
                if (Timer.getFPGATimestamp() - shooterTime >= LOADING_PIN_WAIT)
                {
                    newReloadShooterState = ShooterStateSetFiringArm;
                }
                break;
            }
            case ShooterStateSetFiringArm:
            {
                System.out.println("reloadShooterStateMachine: ShooterStateSetFiringArm");
                tFiringArmIn.set(false);
                tFiringArmOut.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newReloadShooterState = ShooterStateSetFiringArmWait;
                break;
                
            }
            case ShooterStateSetFiringArmWait:
            {
                System.out.println("reloadShooterStateMachine: ShooterStateSetFiringArmWait");
                if (Timer.getFPGATimestamp() - shooterTime >= FIRING_ARM_WAIT)
                {
                    newReloadShooterState = ShooterStateSetFiringPin;
                }   
                break;
            }
            case ShooterStateSetFiringPin:
            {
                System.out.println("reloadShooterStateMachine: ShooterStateSetFiringPin");
                tLoadingPinOut.set(false);
                tLoadingPinIn.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newReloadShooterState = ShooterStateRetractFiringMech;
                break;
            }
            case ShooterStateSetFiringPinWait:
            {
                if (Timer.getFPGATimestamp() - shooterTime >= LOADING_PIN_WAIT)
                {
                    newReloadShooterState = ShooterStateSetFiringPin;
                }
                break;
            }
            case ShooterStateRetractFiringMech:
            {
                System.out.println("reloadShooterStateMachine: ShooterStateRetractFiringMech");
                tFiringArmOut.set(false);
                tFiringArmIn.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newReloadShooterState = ShooterStateRetractFiringMechWait;
                break;
            }
            case ShooterStateRetractFiringMechWait:
            {
                System.out.println("reloadShooterStateMachine: ShooterStateRetractFiringMechWait");
                if (Timer.getFPGATimestamp() - shooterTime >= FIRING_ARM_WAIT)
                {
                    newReloadShooterState = ShooterStateFireReady;
                }
                break;
            }
            case ShooterStateFireReady:
            {
                System.out.println("reloadShooterStateMachine: ShooterStateFireReady");
                if(leftStick.getRawButton(shooterButton))
                {
                    tLoadingPinIn.set(false);
                    tLoadingPinOut.set(true);
                    shooterTime = Timer.getFPGATimestamp();
                    newReloadShooterState = ShooterStateFireWait;
                }                
                break;
            }
            case ShooterStateFireWait:
            {
                if (Timer.getFPGATimestamp() - shooterTime >= FIRING_WAIT)
                {
                    newReloadShooterState = ShooterStateStart;
                }
            }
            default:
            {
                System.out.println("reloadShooterStateMachine: default - ERROR - Should not get here");
                break;
            }
            
        }
        currentReloadShooterState = newReloadShooterState;
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
        //tFiringArm.set(true); //Release the firing arm
        Timer.delay(timingDelay); //Wait just a little bit
        //reloadShooter();
    }

    /**
     * DESCRIPTION: Activates the grabber motor for grabbing
     *              a ball when the grabber button is pressed.
     * ARGUMENTS:   None.
     */
    private void GrabberMotorControls() 
    {
       
        if(leftStick.getRawButton(armControlButton))
        {
            if (previousArmButtonState== false)
            {
                if (previousArmState == false) // arm is in 
                {
                    armIn.set(false);
                    armOut.set(true);
                    previousArmState = true;
                }
                 else {
                      armOut.set(false);
                      armIn.set(true);
                      previousArmState = false; 
                 }
                previousArmButtonState = true;
            }
            
        }
        else {
            previousArmButtonState = false;
        }
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
    private void compressorControl(){
        
        if(!compressor.getPressureSwitchValue()  )
        {
            
            compressor.start();
          
        }
        else
        {
            
            compressor.stop();
        }
         
    }
}
           