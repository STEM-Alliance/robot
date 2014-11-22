// Declare our package (for organizational purposes)
package edu.wpi.first.wpilibj.templates;

// Import the necessary classes
import com.taurus.SwerveController;
import com.taurus.Logger;
import com.taurus.SwerveChassis;
import com.taurus.SwerveConstants;
import com.taurus.SwerveVector;
import com.taurus.SwerveWheel;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
//import edu.wpi.first.wpilibj.Joystick;
//import edu.wpi.first.wpilibj.Timer;
//import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.DriverStationLCD;
//import edu.wpi.first.wpilibj.Joystick.ButtonType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * 
 * @author 
 */
public class RobotTemplate extends IterativeRobot {

    // Motor Objects
    private SwerveChassis drive;
    
    // Joysticks
    private SwerveController controller;

    // Logger
    private Logger log;
    //private static DriverStationLCD DSOutput;

    private boolean TEST = true;
    private final int TEST_MODE_NORMAL = 0;
    private final int TEST_MODE_WHEEL = 1;
    private SendableChooser testChooser;
    private SendableChooser testWheelChooser;
    
    /**
     * runs once the code starts up
     */
    public void robotInit()
    {
        log = new Logger("[Core]", System.out);
        log.info("Initializing main systems...");
        
        drive = new SwerveChassis();

        controller = new SwerveController();
 
        // set up the choosers for running tests while in teleop mode
        testChooser = new SendableChooser();
        testChooser.addDefault("Normal",    Integer.valueOf(TEST_MODE_NORMAL));
        testChooser.addObject("Wheel Test", Integer.valueOf(TEST_MODE_WHEEL));
        
        testWheelChooser = new SendableChooser();
        testWheelChooser.addDefault("Front Left", Integer.valueOf(0));
        testWheelChooser.addObject("Front Right", Integer.valueOf(1));
        testWheelChooser.addObject("Back Right",  Integer.valueOf(2));
        testWheelChooser.addObject("Back Left",   Integer.valueOf(3));
        
        
        log.info("Initialization complete.");
    }

    /**
     * runs when robot is disabled
     */
    public void disabledPeriodic()
    {
        UpdateDashboard();
    }
    
    
    /**
     * runs when operator mode is enabled.
     */
    public void teleopInit()
    {
        log.info("Entering teleoperated mode. Activating controls.");
    }

    /**
     * runs during operator control
     */
    public void teleopPeriodic() 
    {
        
        displayControl();
        
        UpdateDashboard();

        
        if(!TEST)
        {
            DriveNormal();
        }
        else
        {
            TestRun();
        }
    }
    
    /**
     * Update the dashboard with the common entries
     */
    private void UpdateDashboard()
    {
        // display the joysticks on smart dashboard
        SmartDashboard.putNumber("Left Mag",    controller.getMagnitude(Hand.kLeft));
        SmartDashboard.putNumber("Left Angle",  controller.getDirectionDegrees(Hand.kLeft));
        SmartDashboard.putNumber("Right Mag",   controller.getMagnitude(Hand.kRight));
        SmartDashboard.putNumber("Right Angle", controller.getDirectionDegrees(Hand.kRight));
        
        // display and get the PID values for the wheels
        SwerveWheel.DriveP = SmartDashboard.getNumber("Wheel Mag P", SwerveWheel.DriveP);
        SwerveWheel.DriveI = SmartDashboard.getNumber("Wheel Mag I", SwerveWheel.DriveI);
        SwerveWheel.DriveD = SmartDashboard.getNumber("Wheel Mag D", SwerveWheel.DriveD);
        SwerveWheel.AngleP = SmartDashboard.getNumber("Wheel Angle P", SwerveWheel.AngleP);
        SwerveWheel.AngleI = SmartDashboard.getNumber("Wheel Angle I", SwerveWheel.AngleI);
        SwerveWheel.AngleD = SmartDashboard.getNumber("Wheel Angle D", SwerveWheel.AngleD);

        drive.MaxVelocity = SmartDashboard.getNumber("Max Velocity", drive.MaxVelocity);
        
        // show setting for using an xbox controller
        controller.useXbox = SmartDashboard.getBoolean("Xbox Controller", controller.useXbox);
        
        // display current gear
        if(drive.getGearHigh())
        {
            SmartDashboard.putString("Gear", "High");
        }
        else
        {
            SmartDashboard.putString("Gear", "Low");
        }
        // update the test mode
        // disable for competitions?
        TEST = SmartDashboard.getBoolean("TEST MODE", TEST);
        
        if(TEST)
        {
            SmartDashboard.putData("Test Mode", testChooser);
            SmartDashboard.putData("Test Wheel", testWheelChooser);
        }
    }
    
    /**
     * Run the test functions
     */
    private void TestRun()
    {
        switch(((Integer)testChooser.getSelected()).intValue())
        {
            case TEST_MODE_WHEEL:
                TestWheel(((Integer)testWheelChooser.getSelected()).intValue());
                break;
                
            case TEST_MODE_NORMAL:
            default:
                DriveNormal();
                break;
        }
    }
    
    /**
     * Test an individual wheel module
     */
    private void TestWheel(int index)
    {
        // use the left joystick to control the wheel module
        SwerveVector WheelActual = drive.getWheel(index).setDesired(controller.getHaloDrive_Vector());

        // display in SmartDashboard
        SmartDashboard.putNumber("Test Wheel Mag Actual", WheelActual.M());
        SmartDashboard.putNumber("Test Wheel Mag Setpoint", drive.getWheel(index).DrivePID.getSetpoint());
        SmartDashboard.putNumber("Test Wheel Angle Actual", WheelActual.A());
        SmartDashboard.putNumber("Test Wheel Angle Setpoint", drive.getWheel(index).AnglePID.getSetpoint());
        
        // if the button is not held down, we're in high gear
        drive.setGearHigh(controller.getHighGearEnable());
    }
    
    /**
     * Run the normal operating Drive system
     */
    private void DriveNormal()
    {
        // Use the Joystick inputs to update the drive system
        
        drive.Update(controller.getHaloDrive_Vector(), controller.getHaloDrive_Rotation());
        
        // display each wheel's mag and angle in SmartDashboard
        for(int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            SmartDashboard.putNumber("Wheel " + Integer.toString(i) + " Mag", drive.getActual(i).M());
            SmartDashboard.putNumber("Wheel " + Integer.toString(i) + " Angle", drive.getActual(i).A());
        }

        // if the button is not held down, we're in high gear
        drive.setGearHigh(controller.getHighGearEnable());
    }

    /**
     * calls at init of autonomous mode
     */
    public void autonomousInit()
    {
    }

    /**
     * This function is called periodically during autonomous mode.
     */
    public void autonomousPeriodic()
    {
    }

    /**
     * This function is called periodically during test mode.
     */
    public void testPeriodic()
    {
    }

    private void displayControl()
    {
        //DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser1, 1,
        //        "Dist: " + String.valueOf(Math.floor(sonicSignal / 12)) + "ft. "
        //        + String.valueOf(Math.floor(sonicSignal % 12)) + "in.");

        //DriverStationLCD.getInstance().updateLCD();
    }
}
