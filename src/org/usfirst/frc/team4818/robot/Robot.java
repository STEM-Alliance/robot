
package org.usfirst.frc.team4818.robot;

import com.taurus.SwerveApplication;
import com.taurus.SwerveChassis;
import com.taurus.SwerveConstants;
import com.taurus.SwerveVector;
import com.taurus.controller.ControllerChooser;
import com.taurus.controller.ControllerSwerve;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot 
{
    private SwerveApplication application;

    // Test
    private boolean TEST = true;
    private final int TEST_MODE_NORMAL = 0;
    private final int TEST_MODE_WHEEL = 1;
    private final int TEST_MODE_CALIBRATION_1 = 2;
    private final int TEST_MODE_CALIBRATION_2 = 3;
    private SendableChooser testChooser = new SendableChooser();
    private SendableChooser testWheelChooser = new SendableChooser();
    
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() 
    {        
    	application = new com.taurus.SwerveApplication();  // Change for raw swerve or robot year specific
        
        // set up the choosers for running tests while in teleop mode
        testChooser = new SendableChooser();
        testChooser.addDefault("Normal",    Integer.valueOf(TEST_MODE_NORMAL));
        testChooser.addObject("Wheel Test", Integer.valueOf(TEST_MODE_WHEEL));
        testChooser.addObject("Wheel Calibration 1", Integer.valueOf(TEST_MODE_CALIBRATION_1));
        testChooser.addObject("Wheel Calibration 2", Integer.valueOf(TEST_MODE_CALIBRATION_2));
        SmartDashboard.putData("Test", testChooser);
        
        testWheelChooser = new SendableChooser();
        testWheelChooser.addDefault("Front Left", Integer.valueOf(0));
        testWheelChooser.addObject("Front Right", Integer.valueOf(1));
        testWheelChooser.addObject("Back Right",  Integer.valueOf(2));
        testWheelChooser.addObject("Back Left",   Integer.valueOf(3));
        SmartDashboard.putData("Test Wheel", testWheelChooser);
        
        SmartDashboard.putBoolean("TEST MODE", TEST);
    }

    /**
     * runs when robot is disabled
     */
    public void disabledPeriodic()
    {
    	application.disabledPeriodic();
    }
    
    /**
     * runs when robot is disabled
     */
    public void disabledInit()
    {
    	application.disabledInit();
    }
    
    
    /**
     * runs when operator mode is enabled.
     */
    public void teleopInit()
    {
    	application.teleopInit();
    }

    /**
     * runs during operator control
     */
    public void teleopPeriodic() 
    {
        UpdateDashboard();
        
        if(!TEST)
        {
            application.teleopPeriodic();
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
        SmartDashboard.putNumber("Left Mag",    application.controller.getMagnitude(Hand.kLeft));
        SmartDashboard.putNumber("Left Angle",  application.controller.getDirectionDegrees(Hand.kLeft));
        SmartDashboard.putNumber("Right Mag",   application.controller.getMagnitude(Hand.kRight));
        SmartDashboard.putNumber("Right Angle", application.controller.getDirectionDegrees(Hand.kRight));
        
        // display each wheel's mag and angle in SmartDashboard
        for(int i = 0; i < SwerveConstants.WheelCount; i++)
        {
            SmartDashboard.putNumber("Wheel " + Integer.toString(i) + " Mag", application.drive.getWheelActual(i).getMag());
            SmartDashboard.putNumber("Wheel " + Integer.toString(i) + " Angle", application.drive.getWheelActual(i).getAngle());
        }
        
        application.drive.MaxAvailableVelocity = SmartDashboard.getNumber("Max Velocity", application.drive.MaxAvailableVelocity);
        
        //TODO
        //SmartDashboard.putNumber("Gyro Angle", drive.getGyro().getAngle());
                
        // update the test mode
        // disable for competitions?
        TEST = SmartDashboard.getBoolean("TEST MODE", TEST);
    }
    
    /**
     * Run the test functions
     */
    private void TestRun()
    {
        int i = ((Integer)testWheelChooser.getSelected()).intValue();
        
        switch(((Integer)testChooser.getSelected()).intValue())
        {
            case TEST_MODE_WHEEL:
                TestWheel(i);
                break;
                
            case TEST_MODE_CALIBRATION_1:                
            	application.drive.getWheel(i).MotorAngle.set(application.controller.getX(Hand.kRight) * .5);
                SmartDashboard.putNumber("motor set", application.drive.getWheel(i).MotorAngle.get());
                SmartDashboard.putNumber("pot read", application.drive.getWheel(i).AnglePot.get());
                               
                break;
                
            case TEST_MODE_CALIBRATION_2:
            	application.drive.getWheel(i).updateAngleMotor(application.controller.getDirectionDegrees(Hand.kRight), 1.0);
                
                break;
                
            case TEST_MODE_NORMAL:
            default:
                application.teleopPeriodic();
                break;
        }
    }
    
    /**
     * Test an individual wheel module
     */
    private void TestWheel(int index)
    {
        // use the left joystick to control the wheel module
        SwerveVector WheelActual = application.drive.getWheel(index).setDesired(
        		application.controller.getHaloDrive_Velocity(),
        		application.controller.getHighGearEnable(),
        		application.controller.getBrake());

        // display in SmartDashboard
        SmartDashboard.putNumber("Test Wheel Mag Actual", WheelActual.getMag());
        SmartDashboard.putNumber("Test Wheel Angle Actual", WheelActual.getAngle());
        
        // if the button is not held down, we're in high gear
        application.drive.setGearHigh(application.controller.getHighGearEnable());
        application.drive.UpdateShifter();
    }
    
    /**
     * called at init of autonomous mode
     */
    public void autonomousInit()
    {
    	application.autonomousInit();
    }

    /**
     * This function is called periodically during autonomous mode.
     */
    public void autonomousPeriodic()
    {
    	application.autonomousPeriodic();
    }

    /**
     * called at init of test mode
     */
    public void testInit()
    {
    	application.testInit();
    }
    
    /**
     * This function is called periodically during test mode.
     */
    public void testPeriodic()
    {
    	application.testPeriodic();
    }
}
