// Declare our package (for organizational purposes)
package edu.wpi.first.wpilibj.templates;

// Import the necessary classes
import com.taurus.CompressorPins;
import com.taurus.ControlMapping;
import com.taurus.Joysticks;
import com.taurus.Logger;
import com.taurus.SensorPins;
import com.taurus.ServoPins;
import com.taurus.SolenoidPins;
import com.taurus.SwerveChassis;
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
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.DigitalModule;


/**
 * This is a cleaner robot class. It's probably not the best idea to put our
 * robot into a wpilibj package. I like abstraction, organization, and comments.
 * Use what you want, I only rewrote this because I wanted to program some java.
 * Not expecting this to be extra useful.
 *
 * @author the Most Interesting Men in The World (Andrew Vetter and Aaron)
 */
public class RobotTemplate extends IterativeRobot {
    //---------
    //Variables
    //---------

    // Motor Objects
    private SwerveChassis drive;
    private RobotDrive chassis;
    private Victor grabberMotor;
    private final double sensitivity = 1;
    private I2C Slave;
    private final int SlaveAddress = 2;
    
    
    // Solenoid Objects
    private Solenoid firingArmOut; // These four are firing mechanisms
    private Solenoid firingArmIn;
    private Solenoid latchIn;
    private Solenoid latchOut;
    private Solenoid grabberOut;        // These two are for the grabber
    private Solenoid grabberIn;

    // Joysticks
    // TODO Split into an enumeration
    private Joystick leftStick;
    private Joystick rightStick;

    // State machine states
    private final int stShooterStart = 0,
            stShooterRetractFiringPin = 1,
            stShooterRetractFiringPinWait = 2,
            stShooterSetFiringArm = 3,
            stShooterSetFiringArmWait = 4,
            stShooterSetFiringPin = 5,
            stShooterSetFiringPinWait = 6,
            stShooterRetractFiringMech = 7,
            stShooterRetractFiringMechWait = 8,
            stShooterSafety = 9,
            stShooterSafetyLatch = 10,
            stShooterSafetyRetract = 11,
            stShooterFireReady = 12,
            stShooterFireWait = 13;

    private final int stAutoStart = 0,
            stAutoArmRetracting = 1,
            stAutoArmRetractingWait = 2,
            stAutoMoveToPosition = 3,
            stAutoMoveToPositionWait = 4,
            stAutoFire = 5,
            stAutoFireWait = 6,
            stAutoMove = 7,
            stAutoMoveWait = 8,
            stAutoDone = 9;

    // Shooter
    private DigitalInput armSensorL; // Sensors for the shooter state machine
    private DigitalInput armSensorR;
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
    private double servoVertical = .25;

    // Ultrasonic
    private AnalogChannel ultraSonic;
    private double sonicSignal;
    // Delay Constants
    private final double shooterWaitPin = 2.0,
            shooterWaitFire = 2.0,
            autoWaitPosition = 2.8,
            autoWaitFire = 2.0,
            autoWaitMove = 2.0,
            autoWaitGrabber = 2;

    // Speed Constants
    private final double speedStop = 0.0;
    private final double speedGrabberOn = 1.0;
    private final double speedMotorOn = 1.0;

    // Logger
    private Logger log;

    private static DriverStationLCD DSOutput;
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
        
        drive  = new SwerveChassis();
 
        
                
    }

    /**
     * this method starts when operator mode is enabled.
     */
    public void teleopInit() {
        log.info("Entering teleoperated mode. Activating controls.");
        chassis.setSafetyEnabled(true);
        chassis.tankDrive(leftStick, rightStick);
    }
// private final int  I2CButton = 0,
//                    I2CSet = 1, 
//                    I2CGet = 2,
//                    I2CWait = 3; 
// private int I2CState = 0; 
// byte[] send = new byte[5];
// byte[] send2 = new byte[5];
// int motorspeed;
// int motorpostion;
 
        private int wait = 0;
 /**
     * 
     * This function is ran in a loop during operator control.
     */
    public void teleopPeriodic() {
        chassis.tankDrive(leftStick, rightStick);
        //compressorControl();
        //shooterStateMachine(false);
        //grabberControl(false);
        //servoControl();
        //ultrasoundControl();
        displayControl();

        drive.Update(leftStick.getX(), leftStick.getY(), rightStick.getX());
        
        
//        
//        switch(I2CState){
//                
//            case I2CButton: {
//                if(leftStick.getRawButton(ControlMapping.I2CGet)) {
//                    // next state
//                    I2CState = I2CGet; 
//                    log.info("Get");
//                }
//                else if(leftStick.getRawButton(ControlMapping.I2CSet)) {
//                    // next state
//                    I2CState = I2CSet;
//                    log.info("set");
//                }
//                break;
//            }
//            case  I2CGet: {
//                if(Slave.transaction(send2, 0,send, 5) == false)
//                {
//                  motorspeed = send[1] << 8 | send[2]; 
//                  motorpostion = send[3] << 8 | send[4];
//                    
//                    try
//                    {
//                        String a = new String();
//                        a = String.valueOf(send[0]) + " " + String.valueOf(motorspeed ) + " " + String.valueOf(motorpostion);
//                        log.info(a);
//                    }
//                    catch(Exception e)
//                    {
//                        log.info("wtf");
//                    }
//                }
//                else
//                {
//                    log.info("failed");
//                }
//                wait = 0;
//                I2CState = I2CWait;
//                break;       
//            }      
//            case I2CSet: {
//                Slave.write(0, 1);
//                wait = 0;
//                I2CState = I2CWait;
//            }
//            case I2CWait: // wait
//            {
//                wait++;
//                if(wait > 100)
//                {
//                    log.info("Wait Done");
//                    I2CState = I2CButton;
//                }
//                break;
//            }
//        
//        }
    }

    public void autonomousInit() {
        currentAutoState = stAutoStart;
    }

    /**
     * This function is called periodically during autonomous mode.
     */
    public void autonomousPeriodic() {
        autonomousTick();
        compressorControl();
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
    private void shooterStateMachine(boolean autonomous) {
        switch (currentShooterState) {
            case stShooterStart: {
                if (!armSensorR.get() && !armSensorL.get()) {
                    newShooterState = stShooterSetFiringArm;
                }
                if (autonomous) {

                    if (!armSensorL.get() || !armSensorR.get()) {
                        newShooterState = stShooterRetractFiringMech;
                    } else {
                        newShooterState = stShooterRetractFiringPin;
                    }

                } else {
                    log.info("Shooter in starting state.");
                    newShooterState = stShooterRetractFiringPin;
                }
                break;
            }
            case stShooterRetractFiringPin: {
                log.info("Retracting firing pin...");
                latchIn.set(false);
                latchOut.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stShooterRetractFiringPinWait;
                break;
            }
            case stShooterRetractFiringPinWait: {
                if (Timer.getFPGATimestamp() - shooterTime >= shooterWaitPin) {
                    log.info("Firing pin retracted.");
                    newShooterState = stShooterSetFiringArm;
                }
                break;
            }
            case stShooterSetFiringArm: {
                log.info("Setting firing arm...");
                firingArmIn.set(false);
                firingArmOut.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stShooterSetFiringArmWait;
                break;
            }
            case stShooterSetFiringArmWait: {
                if ((armSensorL.get() == false) || (armSensorR.get() == false)) {
                    newShooterState = stShooterSetFiringPin;
                }
                if (leftStick.getRawButton(ControlMapping.latchLeft) && rightStick.getRawButton(ControlMapping.latchRight)) {
                    newShooterState = stShooterSetFiringPin;
                }
                break;
            }
            case stShooterSetFiringPin: {
                log.info("Setting firing pin...");
                latchOut.set(false);
                latchIn.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stShooterSetFiringPinWait;
                break;
            }
            case stShooterSetFiringPinWait: {
                if (Timer.getFPGATimestamp() - shooterTime >= shooterWaitPin) {
                    log.info("Firing pin set.");

                    newShooterState = stShooterRetractFiringMech;

                }
                break;
            }
            case stShooterRetractFiringMech: {
                log.info("Retracting firing mechanism...");
                firingArmOut.set(false);
                firingArmIn.set(true);
                shooterTime = Timer.getFPGATimestamp();
                newShooterState = stShooterRetractFiringMechWait;
                break;
            }
            case stShooterRetractFiringMechWait: {
                if (Timer.getFPGATimestamp() - shooterTime >= shooterWaitPin) {
                    log.info("Firing mechanism set.");
                    newShooterState = stShooterFireReady;
                }
                break;
            }
            case stShooterSafety: {
                firingArmIn.set(false);
                firingArmOut.set(true);
                safetyTime = Timer.getFPGATimestamp();
                newShooterState = stShooterSafetyLatch;
                break;
            }
            case stShooterSafetyLatch: {
                if (Timer.getFPGATimestamp() - safetyTime >= shooterWaitPin) {
                    latchIn.set(false);
                    latchOut.set(true);
                    newShooterState = stShooterSafetyRetract;
                }
                break;
            }
            case stShooterSafetyRetract: {
                firingArmOut.set(false);
                firingArmIn.set(true);
                newShooterState = stShooterStart;
                break;
            }
            case stShooterFireReady: {
                if (leftStick.getRawButton(ControlMapping.fireLeft)
                        && rightStick.getRawButton(ControlMapping.fireRight)) {
                    log.info("Firing shooter!");
                    latchIn.set(false);
                    latchOut.set(true);
                    shooterTime = Timer.getFPGATimestamp();
                    newShooterState = stShooterFireWait;
                } else if (autonomous && currentAutoState == stAutoFire) {
                    latchIn.set(false);
                    latchOut.set(true);
                    shooterTime = Timer.getFPGATimestamp();
                    newShooterState = stShooterFireWait;
                }
                if (leftStick.getRawButton(ControlMapping.releaseLeft) ||
                        rightStick.getRawButton(ControlMapping.releaseRight)) {
                    newShooterState = stShooterSafety;
                }
                break;
            }
            case stShooterFireWait: {
                if (Timer.getFPGATimestamp() - shooterTime >= shooterWaitFire) {
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
    private void grabberControl(boolean autonomous) {
        if (rightStick.getRawButton(ControlMapping.grabberArmDown) && rightStick.getRawButton(ControlMapping.grabberArmUp)) {
            grabberOut.set(false);
            grabberIn.set(true);
        } else if (rightStick.getRawButton(ControlMapping.grabberArmDown) || autonomous) {
            grabberOut.set(true);
            grabberIn.set(false);
        } else if (rightStick.getRawButton(ControlMapping.grabberArmUp)) {
            grabberOut.set(false);
            grabberIn.set(true);
        }

        if (leftStick.getRawButton(ControlMapping.grabberMotorForward) && leftStick.getRawButton(ControlMapping.grabberMotorReverse)) {
            grabberMotor.set(0.0);
        } else if (leftStick.getRawButton(ControlMapping.grabberMotorForward) ) {
            grabberMotor.set(speedGrabberOn);
        } else if (leftStick.getRawButton(ControlMapping.grabberMotorReverse) || autonomous ) {
            grabberMotor.set(-speedGrabberOn);
        } else {
            grabberMotor.set(speedStop);
        }
    }

    /**
     * This function manages the compressor.
     */
    private void compressorControl() {
        // If the tank is low on pressure, stop it. Otherwise make sure its on.
        if (!compressor.getPressureSwitchValue()) {
            compressor.start();
        } else {
            compressor.stop();
        }
    }

    /**
     * This function controls the robot in autonomous mode.
     */
    private void autonomousTick() {

        chassis.setSafetyEnabled(false);
        compressorControl();
        switch (currentAutoState) {
            case stAutoStart: {
                newAutoState = stAutoArmRetracting;
                break;
            }
            case stAutoArmRetracting: {
                log.info("Retracting arm");
                grabberControl(true);
                autoTime = Timer.getFPGATimestamp();
                newAutoState = stAutoArmRetractingWait;
                break;
            }
            case stAutoArmRetractingWait: {
                log.info("Retracting Arm Wait");
                grabberControl(true);
                if (Timer.getFPGATimestamp() - autoTime >= autoWaitGrabber) {
                    newAutoState = stAutoMoveToPosition;

                }
                break;
            }
            case stAutoMoveToPosition: {
                log.info("Moving into firing position.");
                chassis.drive(speedMotorOn / 4, 0);
                autoTime = Timer.getFPGATimestamp();
                newAutoState = stAutoMoveToPositionWait;
                shooterStateMachine(true);
                break;
            }
            case stAutoMoveToPositionWait: {
                if (Timer.getFPGATimestamp() - autoTime >= autoWaitPosition) {
                    chassis.drive(speedStop, 0);
                }
                shooterStateMachine(true);
                if (Timer.getFPGATimestamp() - autoTime >= autoWaitPosition
                        && currentShooterState == stShooterFireReady) {
                    newAutoState = stAutoFire;
                }
                break;
            }
            case stAutoFire: {
                log.info("Firing!");
                shooterStateMachine(true);
                newAutoState = stAutoFireWait;
                autoTime = Timer.getFPGATimestamp();
                break;
            }
            case stAutoFireWait: {
                if (Timer.getFPGATimestamp() - autoTime >= autoWaitFire) {
                    newAutoState = stAutoMove;
                }
                break;
            }
            case stAutoMove: {
                chassis.drive(0 , speedMotorOn);
                autoTime = Timer.getFPGATimestamp();
                newAutoState = stAutoMoveWait;
                break;
            }
            case stAutoMoveWait: {
                if (Timer.getFPGATimestamp() - autoTime >= autoWaitMove) {
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
        chassis = new RobotDrive(1, 2, 3, 4); // Initialize all four drive motors
        grabberMotor = new Victor(5); // Initialize the grabber motor
        chassis.setMaxOutput(sensitivity);
    }

    /**
     * Initialize the sensor subsystem.
     */
    private void initSensors() {
        armSensorL = new DigitalInput(SensorPins.armSensorLeft);
        armSensorR = new DigitalInput(SensorPins.armSensorRight);
    }

    /**
     * Initialize the pneumatics subsystem.
     */
    private void initPneumatics() {
        firingArmIn = new Solenoid(SolenoidPins.firingArmIn);
        firingArmOut = new Solenoid(SolenoidPins.firingArmOut);
        latchIn = new Solenoid(SolenoidPins.loadingPinIn);
        latchOut = new Solenoid(SolenoidPins.loadingPinOut);
        grabberIn = new Solenoid(SolenoidPins.grabberArmIn);
        grabberOut = new Solenoid(SolenoidPins.grabberArmOut);
        compressor = new Compressor(CompressorPins.relay,
                CompressorPins.pressure);
    }

    /**
     * Initialize the drive subsystem.
     */
    private void initDrive() {
        leftStick = new Joystick(Joysticks.left);
        rightStick = new Joystick(Joysticks.right);
        chassis.tankDrive(leftStick, rightStick);
        chassis.setInvertedMotor(RobotDrive.MotorType.kRearLeft, false);
        chassis.setInvertedMotor(RobotDrive.MotorType.kRearRight, false);
    }

    /**
     * Initialize the camera servos
     */
    private void initCamera() {
        servoCamera = new Servo(ServoPins.cameraServo);
    }

    /**
     * Initialize ultrasonic system
     */
    private void initUltrasonic() {
        ultraSonic = new AnalogChannel(1);
    }

    /**
     * This control manages the servos for the camera.
     */
    private void servoControl() {
        if (leftStick.getRawButton(ControlMapping.camUp)) {
            servoVertical = servoVertical + .005;
        } else if (leftStick.getRawButton(ControlMapping.camDown)) {
            servoVertical = servoVertical - .005;
        }
        if (servoVertical > 1) {
            servoVertical = 1;
        }
        if (servoVertical < 0) {
            servoVertical = 0;
        }
        servoCamera.set(servoVertical);

    }

    private void displayControl() {
        DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser1, 1,
                "Dist: " + String.valueOf(Math.floor(sonicSignal / 12)) + "ft. "
                + String.valueOf(Math.floor(sonicSignal % 12)) + "in.");

        DriverStationLCD.getInstance().updateLCD();
        if ((armSensorL.get() == false) || (armSensorR.get() == false)) {
            DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser2, 1,
                    "LATCH SET      ");
        } else {
            DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser2, 1,
                    "LATCH NOT SET");
        }
    }

    private void ultrasoundControl() {
        sonicSignal = ultraSonic.getAverageVoltage();
        sonicSignal = (sonicSignal * 1000) / 11.47;
        log.dbg("Ultrasonic reading: " + String.valueOf(Math.floor(sonicSignal / 12)) + "ft. "
                + String.valueOf(Math.floor(sonicSignal % 12)) + "in.");

    }

}
