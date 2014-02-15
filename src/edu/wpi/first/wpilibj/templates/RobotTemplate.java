// Declare our package (for organizational purposes)
package edu.wpi.first.wpilibj.templates;

// Import the necessary classes
import com.taurus.CompressorPins;
import com.taurus.Joysticks;
import com.taurus.Logger;
import com.taurus.SensorPins;
import com.taurus.ServoPins;
import com.taurus.SolenoidPins;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor; 
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.AnalogChannel;

/**
 * This is a cleaner robot class. It's probably not the best idea to
 * put our robot into a wpilibj package. I like abstraction, organization, and
 * comments. Use what you want, I only rewrote this because I wanted to
 * program some java. Not expecting this to be extra useful.
 * @author Tanner Danzey < arkaniad AT gmail DOT com>
 */
public class RobotTemplate extends IterativeRobot {
    //---------
    //Variables
    //---------
    
    // Motor Objects
    private RobotDrive chassis;
    private Victor grabberMotor;
    private final double sensitivity = 0.5;
    
    // Solenoid Objects
    private Solenoid tFiringArmOut; // These four are firing mechanisms
    private Solenoid tFiringArmIn;
    private Solenoid tLoadingPinIn;
    private Solenoid tLoadingPinOut;
    private Solenoid tGrabberArmOut;        // These two are for the grabber
    private Solenoid tGrabberArmIn;
    
    // Joysticks
    // TODO Split into an enumeration
    private Joystick leftStick;
    private final int shooterButton = 1;
    private final int grabberMotorForwardButton = 2;
    private final int grabberMotorReverseButton = 3;
    private final int bLatchL = 9;
    private final int bReleaseL = 8;
    
    private Joystick rightStick;
    private final int grabberArmOutButton = 2;
    private final int grabberArmInButton = 3;
    private final int shooterButtonSafety = 1;
    private final int bLatchR = 9;
    private final int bReleaseR = 8;
    private final int bCameraUp = 5;
    private final int bCameraDown = 4;
    
    private final int stShooterStart                   = 0;
    private final int stShooterRetractFiringPin        = 1;
    private final int stShooterRetractFiringPinWait    = 2;
    private final int stShooterSetFiringArm            = 3;
    private final int stShooterSetFiringArmWait        = 4;
    private final int stShooterSetFiringPin            = 5;
    private final int stShooterSetFiringPinWait        = 6;
    private final int stShooterRetractFiringMech       = 7;
    private final int stShooterRetractFiringMechWait   = 8;
    private final int stShooterSafety                  = 9;
    private final int stShooterSafetyLatch             = 10;
    private final int stShooterSafetyRetract           = 11;
    private final int stShooterFireReady               = 12;
    private final int stShooterFireWait                = 13;
    
    private final int stAutoStart = 0;
    private final int stAutoArmRetracting = 1;
    private final int stAutoMoveToPosition = 2;
    private final int stAutoMoveToPositionWait = 3;
    private final int stAutoFire = 4;
    private final int stAutoFireWait = 5;
    private final int stAutoMove = 6;
    private final int stAutoMoveWait = 7;
    private final int stAutoDone = 8;
    
    // Shooter
    private DigitalInput sArmL; // Sensors for the shooter state machine
    private DigitalInput sArmR;
    private DigitalInput sPistonL;
    private DigitalInput sPistonR;
    private DigitalInput sLatch;
    private Compressor compressor;
    private int currentShooterState = 0;
    private int newShooterState = 0;
    private double shooterTime = 0;
    private double safetyTime = 0;
    
    // Autonomous
    private int currentAutoState = 0;
    private int newAutoState = 0;
    private double autoTime = 0;
    
    // Camera
    private AxisCamera camera;
    private final String cameraIP = "10.48.18.11";
    private DriverStation driverStation;
    private Servo servoCamera;
    private double servoVertical = .5;
    
    // Ultrasonic
    private AnalogChannel sSonic;
    private double sonicSignal;
    
    // Constants
    private final double timingDelay = 0.5;
    boolean motorInverted = true;
    private final double speedStop = 0.0;
    private final double speedGrabberOn = 1.0;
    private final double speedMotorOn = 1.0;
    
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
        initUltrasonic();
        initCamera();
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
        compressorTick(); 
        shooterStateTick(false); 
        grabberStateTick(false); 
        driveControlTick();
        servoTick();
        ultrasoundTick();
        
    }
    /**
     * This function is called periodically during autonomous mode.
     */
    public void autonomousPeriodic() {
        autonomousTick();
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
    private void shooterStateTick(boolean autonomous) {      
        final double waitPin = 2;
        final double waitFire = 2;
        
        switch(currentShooterState) {
            case stShooterStart: {
                if(autonomous) {
                    log.info("Shooter in autonomous starting state.");
                    if(sLatch.get()) {
                        if(sArmL.get() && sArmR.get()) {
                            newShooterState = stShooterSetFiringPin;
                        } else {
                            newShooterState = stShooterSetFiringArm;
                        }
                    } else {
                        if(sArmL.get() && sArmR.get()) {
                            newShooterState = stShooterRetractFiringMech;
                        } else {
                            newShooterState = stShooterRetractFiringPin;
                        }
                    }
                } else {
                    log.info("Shooter in starting state.");
                    newShooterState = stShooterRetractFiringPin;
                }
                break;
            }
            case stShooterRetractFiringPin: {
                log.info("Retracting firing pin...");
                tLoadingPinIn.set(false);
                tLoadingPinOut.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stShooterRetractFiringPinWait;
                break;
            }
            case stShooterRetractFiringPinWait: {
                if(Timer.getFPGATimestamp() - shooterTime >= waitPin) {
                    log.info("Firing pin retracted.");
                    newShooterState = stShooterSetFiringArm;
                }
                break;
            }
            case stShooterSetFiringArm: {
                log.info("Setting firing arm...");
                tFiringArmIn.set(false);
                tFiringArmOut.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stShooterSetFiringArmWait;
                break;
            }
            case stShooterSetFiringArmWait: {
                if (sArmR.get() && sArmL.get()) {
                    newShooterState = stShooterSetFiringPin;
                }
                if (leftStick.getRawButton(bLatchL) && rightStick.getRawButton(bLatchR)) {
                    newShooterState = stShooterSetFiringPin;
                }
                break;
            }
            case stShooterSetFiringPin: {
                log.info("Setting firing pin...");
                tLoadingPinOut.set(false);
                tLoadingPinIn.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stShooterSetFiringPinWait;
                break;
            }
            case stShooterSetFiringPinWait: {
                if(Timer.getFPGATimestamp() - shooterTime >= waitPin) {
                    log.info("Firing pin set.");
                    newShooterState = stShooterRetractFiringMech;
                }
                break;
            }
            case stShooterRetractFiringMech: {
                log.info("Retracting firing mechanism...");
                tFiringArmOut.set(false);
                tFiringArmIn.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stShooterRetractFiringMechWait;
                break;
            }
            case stShooterRetractFiringMechWait: {
                if(sPistonL.get() && sPistonR.get() ) {
                    log.info("Firing mechanism set.");
                    newShooterState = stShooterFireReady;
                }
                break;
            }
            case stShooterSafety: {
                tFiringArmIn.set(false);
                tFiringArmOut.set(true);
                safetyTime = Timer.getFPGATimestamp();
                newShooterState = stShooterSafetyLatch;
                break;
            }
            case stShooterSafetyLatch: {
                if(Timer.getFPGATimestamp() - safetyTime >= waitPin) {
                    tLoadingPinIn.set(false);
                    tLoadingPinOut.set(true);
                    newShooterState = stShooterSafetyRetract;
                }
                break;
            }
            case stShooterSafetyRetract: {
                tFiringArmOut.set(false);
                tFiringArmIn.set(true);
                newShooterState = stShooterStart;
                break;
            }
            case stShooterFireReady: {
                if(leftStick.getRawButton(shooterButton) && 
                   rightStick.getRawButton(shooterButtonSafety)) {
                    log.info("Firing shooter!");
                    tLoadingPinIn.set(false);
                    tLoadingPinOut.set(true);
                    shooterTime = Timer.getFPGATimestamp();
                    newShooterState = stShooterFireWait;
                } else if (autonomous && currentAutoState == stAutoFire) {
                    tLoadingPinIn.set(false);
                    tLoadingPinOut.set(true);
                    shooterTime = Timer.getFPGATimestamp();
                    newShooterState = stShooterFireWait;
                }
                if(leftStick.getRawButton(bReleaseL) && rightStick.getRawButton(bReleaseR)) {
                    newShooterState = stShooterSafety;
                }
                break;
            }
            case stShooterFireWait: {
                if(Timer.getFPGATimestamp() - shooterTime >= waitFire ) {
                    log.info("Reloading shooter.");
                    newShooterState = stShooterStart;
                }
                break;
            }
            default: {
                log.error("Shooter should never be in this state. SOS.");
                break;
            }
        }
        currentShooterState = newShooterState;
    }
    
    /**
     * This function manages the state machine for the grabber arm
     */
    private void grabberStateTick(boolean autonomous) {
        if(rightStick.getRawButton(grabberArmOutButton) && rightStick.getRawButton(grabberArmInButton)) {
           // If both buttons are pressed, report an error.
           log.error("Too many buttons pressed, grabber arm cannot exist in two positions simultaneously!");
           tGrabberArmOut.set(false);
           tGrabberArmIn.set(true);
        } else if (rightStick.getRawButton(grabberArmOutButton) || autonomous) {
           log.info("Arm extended.");
           tGrabberArmOut.set(true);
           tGrabberArmIn.set(false);
        } else if(rightStick.getRawButton(grabberArmInButton)) {
            log.info("Arm retracted.");
            tGrabberArmOut.set(false);
            tGrabberArmIn.set(true);
        }
        
        if(leftStick.getRawButton(grabberMotorForwardButton) && leftStick.getRawButton(grabberMotorReverseButton)) {
           log.error("Too many buttons pressed, grabber motor cannot exist in two states!");
           grabberMotor.set(0.0);
        } else if (leftStick.getRawButton(grabberMotorForwardButton)) {
            log.info("Grabber motor forward");
            grabberMotor.set(speedGrabberOn); 
        } else if (leftStick.getRawButton(grabberMotorReverseButton)) {
            log.info("Grabber motor reverse");
            grabberMotor.set(-speedGrabberOn);
        } else { 
            grabberMotor.set(0.0);
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
    
    private void driveControlTick() {
        // TODO reversed controls
    }
    /**
     * This function controls the robot in autonomous mode.
     */
    private void autonomousTick() {        
        final int autoPositionWait = 2;
        final int autoShooterWait = 2;
        final int autoMoveWait = 2;
        
        chassis.setSafetyEnabled(false);
        compressorTick();
        switch (currentAutoState) {
            case stAutoStart: {
                newAutoState = stAutoArmRetracting;
                break;
            }
            case stAutoArmRetracting: {
                log.info("Retracting arm");
                grabberStateTick(true);
                newAutoState = stAutoMoveToPosition;
                break;
            }
            case stAutoMoveToPosition: {
                log.info("Moving into firing position.");
                chassis.drive(speedMotorOn, 0);
                autoTime = Timer.getFPGATimestamp();
                newAutoState = stAutoMoveToPositionWait;
                shooterStateTick(true);
                break;
            }
            case stAutoMoveToPositionWait: {
                if (Timer.getFPGATimestamp() - autoTime >= autoPositionWait ) {
                    chassis.drive(speedStop, 0);
                }
                shooterStateTick(true);
                if (Timer.getFPGATimestamp() - autoTime >= autoPositionWait 
                 && currentShooterState == stShooterFireReady) {
                    //TODO Fix this magic number
                    newAutoState = stAutoFire;
                }
                break;
            }
            case stAutoFire: {
                log.info("Firing!");
                shooterStateTick(true);
                newAutoState = stAutoFireWait;
                autoTime = Timer.getFPGATimestamp();
                break;
            }
            case stAutoFireWait: {
                if(Timer.getFPGATimestamp() - autoTime >= autoShooterWait ) {
                    newAutoState = stAutoMove;
                }
                break;
            }
            case stAutoMove: {
                log.info("Moving after firing..");
                chassis.drive(speedMotorOn, 0);
                autoTime = Timer.getFPGATimestamp();
                newAutoState = stAutoMoveWait;
                break;
            }
            case stAutoMoveWait: {
                if(Timer.getFPGATimestamp() - autoTime >= autoMoveWait) {
                    chassis.drive(speedStop, 0);
                    newAutoState = stAutoDone;
                }
                break;
            }
            case stAutoDone: {
                break;
            }
        }
        currentAutoState = newAutoState;
    }
    
    /**
     * Initialize the motor subsystem.
     */
    private void initMotors() {
        log.info("Initializing motors...");
        chassis = new RobotDrive(1,2,3,4); // Initialize all four drive motors
        grabberMotor = new Victor(5); // Initialize the grabber motor
        chassis.setInvertedMotor(RobotDrive.MotorType.kRearLeft, motorInverted);
        chassis.setMaxOutput(sensitivity);
    }
    /**
     * Initialize the sensor subsystem.
     */
    private void initSensors() {
        log.info("Initializing sensors...");
        sArmL = new DigitalInput(SensorPins.armSensorLeft);
        sArmR = new DigitalInput(SensorPins.armSensorRight);
        sPistonL = new DigitalInput(SensorPins.armPistonLeft);
        sPistonR = new DigitalInput(SensorPins.armPistonRight);
        sLatch = new DigitalInput(SensorPins.latch);
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
        chassis.tankDrive(leftStick, rightStick);
    }
    
    /**
     * Initialize the camera servos
     */
    private void initCamera() {
        log.info("Initializing camera servo...");
        servoCamera = new Servo(ServoPins.cameraServo);
    }
    
    /**
     * Initialize ultrasonic system
     */
    private void initUltrasonic() {
        log.info("Initializing ultrasonic sensor...");
        sSonic = new AnalogChannel(1);
    }
    
    private void servoTick() {
        if(leftStick.getRawButton(bCameraUp)) {
            servoVertical = servoVertical +.1;
        } else if (leftStick.getRawButton(bCameraDown)) {
            servoVertical = servoVertical -.1;
        }
        servoCamera.set(servoVertical);
    }
    
    private void ultrasoundTick() {
        sonicSignal = sSonic.getAverageVoltage();
        sonicSignal = ( sonicSignal * 100) / 9.8 ;
        log.dbg("Ultrasonic reading: " + String.valueOf(sonicSignal));
    }
}
