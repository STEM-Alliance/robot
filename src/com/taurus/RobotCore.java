// Declare our package (for organizational purposes)
package com.taurus;

// Import the necessary classes
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor; 

/**
 * This is a cleaner robot class. It's probably not the best idea to
 * put our robot into a wpilibj package. I like abstraction, organization, and
 * comments. Use what you want, I only rewrote this because I wanted to
 * program some java. Not expecting this to be extra useful.
 * @author Tanner Danzey < arkaniad AT gmail DOT com>
 */
public class RobotCore extends IterativeRobot {
    //---------
    //Variables
    //---------
    
    // Motor Objects
    private RobotDrive chassis;
    private Victor grabberMotor;
    
    // Solenoid Objects
    private Solenoid tFiringArmOut; // These four are firing mechanisms
    private Solenoid tFiringArmIn;
    private Solenoid tLoadingPinIn;
    private Solenoid tLoadingPinOut;
    private Solenoid tGrabberArmOut;        // These two are for the grabber
    private Solenoid tGrabberArmIn;
    
    // Joysticks
    private Joystick leftStick;
    private final int shooterButton = 1;
    private final int grabberButton = 2;
    private final int grabberArmButton = 3;
    
    private Joystick rightStick;
    
    // State variables
    private boolean previousGrabberState = false;
    private boolean grabberState = false; // necessary?
    private boolean previousArmState = false;
    private boolean previousArmButtonState = false; // questionable
    
    // Shooter
    private DigitalInput sFiringArm; // Sensors for the shooter state machine
    private DigitalInput sLoadingPin; 
    private Compressor compressor;
    private int currentShooterState = 0;
    private int newShooterState = 0;
    private double shooterTime = 0;
    
    // Camera
    private AxisCamera camera;
    private final String cameraIP = "10.48.18.11";
    
    // Constants
    private final double timingDelay = 0.5;
    boolean motorInverted = true;
    private final double speedStop = 0.0;
    private final double speedGrabberOn = 1.0;
    
    // Logger
    private Logger log;
    
    //-----------------
    // Public Functions
    //-----------------
    
    /**
     * This method is the first to run once the code starts up.
     */
    public void robotInit() {
        log = new Logger("[Core]", System.out);
        log.info("Initializing main systems...");
        initMotors();
        initSensors();
        initPneumatics();
        initDrive();
        log.info("Initialization complete.");
    }
    /**
     * this method starts when operator mode is enabled.
     */
    public void teleopInit() {
        log.info("Entering teleoperated mode. Activating controls.");
        chassis.setSafetyEnabled(true);
        chassis.tankDrive(leftStick, rightStick);
    }
    /**
     * This function is ran in a loop during operator control.
     */
    public void teleopPeriodic() {
        chassis.tankDrive(leftStick, rightStick);
        compressorTick(); // TODO Implement compressor controls
        shooterStateTick(); // TODO Implement shooter state
        grabberStateTick(); // TODO Implement grabber state
    }
    /**
     * This function is called periodically during autonomous mode.
     */
    public void autonomousPeriodic() {
        // TODO Implement a proper autonomous mode
        chassis.setSafetyEnabled(false);
        chassis.drive(0.5, 0);
        Timer.delay(2.0);
        chassis.drive(0.0,0.0);
    }
    /**
     * This function is called periodically during test mode.
     */
    public void testPeriodic() {
        
    }
    
    //------------------
    // Private Functions
    //------------------
    
    /**
     * This function manages the state machine for the shooter arm.
     */
    private void shooterStateTick() {
        final int stStart                   = 0;
        final int stRetractFiringPin        = 1;
        final int stRetractFiringPinWait    = 2;
        final int stSetFiringArm            = 3;
        final int stSetFiringArmWait        = 4;
        final int stSetFiringPin            = 5;
        final int stSetFiringPinWait        = 6;
        final int stRetractFiringMech       = 7;
        final int stRetractFiringMechWait   = 8;
        final int stFireReady               = 9;
        final int stFireWait                = 10;
        
        final double waitPin = 2;
        final double waitFire = 2;
        final double waitFireArm = 10;
        
        switch(currentShooterState) {
            case stStart: {
                log.info("Shooter in starting state.");
                newShooterState = stRetractFiringPin;
                break;
            }
            case stRetractFiringPin: {
                log.info("Retracting firing pin...");
                tLoadingPinIn.set(false);
                tLoadingPinOut.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stRetractFiringPinWait;
                break;
            }
            case stRetractFiringPinWait: {
                if(Timer.getFPGATimestamp() - shooterTime >= waitPin) {
                    log.info("Firing pin retracted.");
                    newShooterState = stSetFiringArm;
                }
                break;
            }
            case stSetFiringArm: {
                log.info("Setting firing arm...");
                tFiringArmIn.set(false);
                tFiringArmOut.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stSetFiringArmWait;
                break;
            }
            case stSetFiringArmWait: {
                if(Timer.getFPGATimestamp() - shooterTime >= waitFireArm ) {
                    log.info("Firing arm set.");
                    newShooterState = stSetFiringPin;
                }
                break;
            }
            case stSetFiringPin: {
                log.info("Setting firing pin...");
                tLoadingPinOut.set(false);
                tLoadingPinIn.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stSetFiringPinWait;
                break;
            }
            case stSetFiringPinWait: {
                if(Timer.getFPGATimestamp() - shooterTime >= waitPin) {
                    log.info("Firing pin set.");
                    newShooterState = stRetractFiringMech;
                }
                break;
            }
            case stRetractFiringMech: {
                log.info("Retracting firing mechanism...");
                tFiringArmOut.set(false);
                tFiringArmIn.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stRetractFiringMechWait;
                break;
            }
            case stRetractFiringMechWait: {
                if(Timer.getFPGATimestamp() - shooterTime >= waitFireArm ) {
                    log.info("Firing mechanism set.");
                    newShooterState = stFireReady;
                }
                break;
            }
            case stFireReady: {
                if(leftStick.getRawButton(shooterButton)) {
                    log.info("Firing shooter!");
                    tLoadingPinIn.set(false);
                    tLoadingPinOut.set(true);
                    shooterTime = Timer.getFPGATimestamp();
                    newShooterState = stFireWait;
                }
                break;
            }
            case stFireWait: {
                if(Timer.getFPGATimestamp() - shooterTime >= waitFire ) {
                    log.info("Reloading shooter.");
                    newShooterState = stStart;
                }
                break;
            }
            default: {
                log.error("Shooter should never be in this state. SOS.");
                break;
            }
        }
    }
    
    /**
     * This function manages the state machine for the grabber arm
     */
    private void grabberStateTick() {
        // TODO could be implemented slightly better.
        if(leftStick.getRawButton(grabberArmButton)) {
            log.info("Toggling grabber arm...");
            if(!previousArmState) {
                // arm is currently in
                tGrabberArmIn.set(false);
                tGrabberArmOut.set(true);
                previousArmState = true;
            } else {
                // arm is currently out
                tGrabberArmOut.set(false);
                tGrabberArmIn.set(true);
                previousArmState = false;
            }
        }
        if(leftStick.getRawButton(grabberButton)) {
            log.info("Toggling grabber motor...");
            if(previousGrabberState) {
                previousGrabberState = false;
                grabberMotor.set(speedStop);
            } else {
                previousGrabberState = true;
                grabberMotor.set(speedGrabberOn);
            }
        }
    }
    
    /**
     * This function manages the compressor.
     */
    private void compressorTick() {
        // If the tank is low on pressure, stop it. Otherwise make sure its on.
        if(!compressor.getPressureSwitchValue()) {
            compressor.start();
        } else {
            compressor.stop();
        }
    }
    
    /**
     * Initialize the motor subsystem.
     */
    private void initMotors() {
        log.info("Initializing motors...");
        chassis = new RobotDrive(1,2,3,4); // Initialize all four drive motors
        grabberMotor = new Victor(5); // Initialize the grabber motor
        chassis.setInvertedMotor(RobotDrive.MotorType.kRearLeft, motorInverted);
    }
    /**
     * Initialize the sensor subsystem.
     */
    private void initSensors() {
        log.info("Initializing sensors...");
        sFiringArm = new DigitalInput(SensorPins.firingArm);
        sLoadingPin = new DigitalInput(SensorPins.loadingPin);
    }
    /**
     * Initialize the pneumatics subsystem.
     */
    private void initPneumatics() {
        log.info("Initializing solenoids...");
        tFiringArmIn = new Solenoid(SolenoidPins.firingArmIn);
        tFiringArmOut = new Solenoid(SolenoidPins.firingArmOut);
        tLoadingPinIn = new Solenoid(SolenoidPins.loadingPinIn);
        tLoadingPinOut = new Solenoid(SolenoidPins.loadingPinOut);
        tGrabberArmIn = new Solenoid(SolenoidPins.grabberArmIn);
        tGrabberArmOut = new Solenoid(SolenoidPins.grabberArmOut);
        log.info("Initializing compressor...");
        compressor = new Compressor(CompressorPins.relay,
                                    CompressorPins.pressure);
    }
    /**
     * Initialize the drive subsystem.
     */
    private void initDrive() {
        log.info("Initializing drive subsystem...");
        leftStick = new Joystick(Joysticks.left);
        rightStick = new Joystick(Joysticks.right);
    }
}
