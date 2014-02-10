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
import edu.wpi.first.wpilibj.DriverStation; 


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
    
  
    
    /* Left Joystick Setup */
    private Joystick leftStick;
    private final int shooterButton = 1;
    private final int grabberForwardButton = 3;
    private final int grabberReverseButton = 2;
    //private final int compressorButton = 11;
    
    /* Right Joystick Setup */
    private Joystick rightStick;
    private final int armOutControlButton = 3;
    private final int armInControlButton = 2;
    
    private boolean previousGrabberState = false;
    private final boolean grabber = false;
    private final double grabberSpeed = 1.0;
    private boolean previousArmState = false;
    private boolean previousArmButtonState = false;
    
    
    
    /* Shooter */
    private DigitalInput armSensorR;
    private DigitalInput armSensorL;
    private DigitalInput armPistonL;
    private DigitalInput armPistonR;
    private DigitalInput latchSensor;
    private boolean firingArm = false;
    private boolean loadingPin = false;
    //private boolean previousCompressorState = false;
    private boolean firingReady = false;
    private final double timingDelay = 0.5;
    private Compressor compressor;
    
    /* Camera */
    private AxisCamera camera;
    private final String cameraIP = "10.48.18.11";
    private DriverStation driverStation;
   
    ///////////////////////////////////////////////////////
    //  PUBLIC METHODS
    ///////////////////////////////////////////////////////
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() 
    {
        
        // Initialize Objects
        chassis = new RobotDrive(1, 2, 3, 4);
        grabberMotor = new Victor(5);
        
        leftStick = new Joystick(1);
        rightStick = new Joystick(2);
        
        //    Firing arm
        armSensorL = new DigitalInput(2);
        armSensorR = new DigitalInput(3);
        armPistonL = new DigitalInput(4);
        armPistonR = new DigitalInput(5);
        latchSensor = new DigitalInput(6);
        tFiringArmIn  = new Solenoid(1);
        tFiringArmOut = new Solenoid(2);
        tLoadingPinIn = new Solenoid(3);
        tLoadingPinOut  = new Solenoid(4);
        armOut = new Solenoid(5);
        armIn = new Solenoid(6);
       
      
        compressor = new Compressor(1,1);             
        
        camera = AxisCamera.getInstance(cameraIP);
        
        
        //driverStation = DriverStation.getInstance();
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
        chassis.setMaxOutput(1);
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
        ShooterStateMachine(false);
        GrabberArmControls(false);
       
        
        
    }

    /**
     * This function is called periodically during autonomous
     */
    private final int AutoStateStart =                   0;
    private final int AutoStateArmRetracting =           1;
    private final int AutoStateMoveToPosition =          3;
    private final int AutoStateMoveToPositionWait =      4;
    private final int AutoStateFireShooter =             5;
    private final int AutoStateFireShooterWait =         6;
    private final int AutoStateMove =                    7;
    private final int AutoStateMoveWait =                8;
    private final int AutoStateDone =                    9;
    
    private int currentAutoState = AutoStateStart;
    private int newAutoState = AutoStateStart;
    private double autoTime = 0;
    private final int AUTO_MOVE_TO_POSITION_TIME = 2;
    private final int AUTO_SHOOTER_WAIT_TIME = 2;
    private final int AUTO_MOVE_TIME = 2;
    public void autonomousPeriodic() 
    {
        chassis.setSafetyEnabled(false);
        compressorControl();
        switch (currentAutoState)
        {
            case AutoStateStart:
            {    
                newAutoState = AutoStateArmRetracting;
            break;
            }
            case AutoStateArmRetracting:
            {
                GrabberArmControls(true);
                newAutoState = AutoStateMoveToPosition;
               break;
            }
            case AutoStateMoveToPosition:
            {
                chassis.drive(1, 0);
                autoTime = Timer.getFPGATimestamp();
                newAutoState = AutoStateMoveToPositionWait;
                ShooterStateMachine(true);
                break;
            }
            case AutoStateMoveToPositionWait:
            {
                 if (Timer.getFPGATimestamp() - autoTime >= AUTO_MOVE_TO_POSITION_TIME)
                {
                    chassis.drive(0, 0);
                    newAutoState = AutoStateFireShooter;
                }
                 ShooterStateMachine(true);
                if ((Timer.getFPGATimestamp() - autoTime >= AUTO_MOVE_TO_POSITION_TIME)&& 
                        (currentReloadShooterState == ShooterStateFireReady)){
                    newAutoState = AutoStateFireShooter;
                }
                 break;
            }
            case AutoStateFireShooter:
            {
                 ShooterStateMachine(true);
                 newAutoState = AutoStateFireShooterWait;
                 autoTime = Timer.getFPGATimestamp();
                 break;
            }
            case AutoStateFireShooterWait:
            {
                 if (Timer.getFPGATimestamp() - autoTime >= AUTO_SHOOTER_WAIT_TIME)
                {
                    
                    newAutoState = AutoStateMove;
                }
                 break;
            }
            case AutoStateMove:
            {
                chassis.drive(1, 0);
                autoTime = Timer.getFPGATimestamp();
                newAutoState = AutoStateMoveWait;
                break;
            }
            case AutoStateMoveWait:
            {
                 if (Timer.getFPGATimestamp() - autoTime >= AUTO_MOVE_TIME)
                {
                    chassis.drive(0, 0);
                    newAutoState = AutoStateDone;
                }
                 break;
            }
            case AutoStateDone:
            {
                
                break;
            }
        }
        currentAutoState = newAutoState;

// Drive forward out of the zone and stop
       
        
       
        
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
    
    //private final int FIRING_ARM_WAIT = 10;
    private final int LOADING_PIN_WAIT = 2;
    private final int FIRING_WAIT = 2;
    
    /**
     * DESCRIPTION: Activate the controls for the 
     *              shooter.
     * ARGUMENTS:   None.
     */
    private void ShooterStateMachine(boolean autoMode)
    {
        switch(currentReloadShooterState)
        {
            case ShooterStateStart:
            {
                if (autoMode == true)
                {
                    if (latchSensor.get())
                    {
                        if (armSensorL.get() && armSensorR.get())
                        {
                            newReloadShooterState = ShooterStateSetFiringPin;
                        }
                        else 
                        {
                            newReloadShooterState = ShooterStateSetFiringArm;
                        }
                       
                    }
                    else
                    {
                         if (armSensorL.get() && armSensorR.get())
                        {
                            newReloadShooterState = ShooterStateRetractFiringMech;
                        }
                        else 
                        {
                            newReloadShooterState = ShooterStateRetractFiringPin;
                        }
                    }
                }
                else
                {
                //System.out.println("reloadShooterStateMachine: ShooterStateStart");
                newReloadShooterState = ShooterStateRetractFiringPin;
                }
                break;
            }
            case ShooterStateRetractFiringPin:
            {
                //System.out.println("reloadShooterStateMachine: ShooterStateRetractFiringPin");
                tLoadingPinIn.set(false);
                tLoadingPinOut.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newReloadShooterState = ShooterStateRetractFiringPinWait;
                break;
            }
            case ShooterStateRetractFiringPinWait:
            {
                //System.out.println("reloadShooterStateMachine: ShooterStateRetractFiringPinWait");
                if (latchSensor.get())
                {
                    newReloadShooterState = ShooterStateSetFiringArm;
                }
                break;
            }
            case ShooterStateSetFiringArm:
            {
                //System.out.println("reloadShooterStateMachine: ShooterStateSetFiringArm");
                tFiringArmIn.set(false);
                tFiringArmOut.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newReloadShooterState = ShooterStateSetFiringArmWait;
                break;
            }
            case ShooterStateSetFiringArmWait:
            {
               // System.out.println("reloadShooterStateMachine: ShooterStateSetFiringArmWait");
                if (armSensorR.get()&& armSensorL.get())
                {
                    newReloadShooterState = ShooterStateSetFiringPin;
                }   
                break;
            }
            case ShooterStateSetFiringPin:
            {
               // System.out.println("reloadShooterStateMachine: ShooterStateSetFiringPin");
                tLoadingPinOut.set(false);
                tLoadingPinIn.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newReloadShooterState = ShooterStateSetFiringPinWait;
                break;
            }
            case ShooterStateSetFiringPinWait:
            {
                if (Timer.getFPGATimestamp() - shooterTime >= LOADING_PIN_WAIT)
                {
                    newReloadShooterState = ShooterStateRetractFiringMech;
                }
                break;
            }
            case ShooterStateRetractFiringMech:
            {
                //System.out.println("reloadShooterStateMachine: ShooterStateRetractFiringMech");
                tFiringArmOut.set(false);
                tFiringArmIn.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newReloadShooterState = ShooterStateRetractFiringMechWait;
                break;
            }
            case ShooterStateRetractFiringMechWait:
            {
                //System.out.println("reloadShooterStateMachine: ShooterStateRetractFiringMechWait");
                if (armPistonL.get() && armPistonR.get())
                {
                    newReloadShooterState = ShooterStateFireReady;
                }
                break;
            }
            case ShooterStateFireReady:
            {
                //System.out.println("reloadShooterStateMachine: ShooterStateFireReady");
                if(leftStick.getRawButton(shooterButton))
                {
                    tLoadingPinIn.set(false);
                    tLoadingPinOut.set(true);
                    shooterTime = Timer.getFPGATimestamp();
                    newReloadShooterState = ShooterStateFireWait;
                } 
                else if ((autoMode == true)&& ( currentAutoState == AutoStateFireShooter))
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
    private void GrabberArmControls(boolean autoMode) 
    {
        if(rightStick.getRawButton(armOutControlButton) && rightStick.getRawButton(armInControlButton)) 
        {
           // if both buttons are pressed report error
           System.out.println("error");
           armOut.set(false);
           armIn.set(true);
        }
        else if (rightStick.getRawButton(armOutControlButton)|| (autoMode == true)) 
        {
           System.out.println("Arm Out/n");
           armOut.set(true);
           armIn.set(false);
        }
        else if(rightStick.getRawButton(armInControlButton))
        {
            System.out.println("Arm In/n");
            armOut.set(false);
            armIn.set(true);
        }
        else 
        {
            // Do nothing 
        }
        
        if(leftStick.getRawButton(grabberForwardButton) && leftStick.getRawButton(grabberReverseButton))
        {
           System.out.println("Error/n");
            grabberMotor.set(0.0);
        }
        else if (leftStick.getRawButton(grabberForwardButton))
        {
            System.out.println("Grabber Forward/n");
            grabberMotor.set(grabberSpeed); 
        }
        else if (leftStick.getRawButton(grabberReverseButton))
        {
            System.out.println("Grabber Reverse /n");
            grabberMotor.set(-grabberSpeed);
        }
        else
        { 
            grabberMotor.set(0.0);
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
           