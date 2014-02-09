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
    
    // Camera
    private AxisCamera camera;
    private final String cameraIP = "10.48.18.11";
    
    // Constants
    private final double timingDelay = 0.5;
    boolean motorInverted = true;
    
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
        // TODO Implement
    }
    /**
     * This function manages the state machine for the grabber arm
     */
    private void grabberStateTick() {
        // TODO Implement
    }
    
    /**
     * This function manages the compressor.
     */
    private void compressorTick() {
        // TODO Implement
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
