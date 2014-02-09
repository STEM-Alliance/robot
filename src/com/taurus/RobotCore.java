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
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.Relay;
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
    private Solenoid armOut;        // These two are for the grabber
    private Solenoid armIn;
    
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
        
        
    }
    
    
}
