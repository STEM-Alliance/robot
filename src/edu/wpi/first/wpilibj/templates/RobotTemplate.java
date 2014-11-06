// Declare our package (for organizational purposes)
package edu.wpi.first.wpilibj.templates;

// Import the necessary classes
import com.taurus.Joysticks;
import com.taurus.Logger;
import com.taurus.SwerveChassis;
import com.taurus.SwerveConstants;
import com.taurus.SwerveVector;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;
import edu.wpi.first.wpilibj.Joystick.ButtonType;


/**
 * 
 * @author 
 */
public class RobotTemplate extends IterativeRobot {
    //---------
    //Variables
    //---------

    // Motor Objects
    private SwerveChassis drive;
    
    // Joysticks
    private Joystick leftStick;
    private Joystick rightStick;

    // Logger
    private Logger log;
    private static DriverStationLCD DSOutput;

    //-----------------
    // Public Functions
    //-----------------
    /**
     * This method is the first to run once the code starts up.
     */
    public void robotInit()
    {
        log = new Logger("[Core]", System.out);
        log.info("Initializing main systems...");
        
        drive = new SwerveChassis();

        leftStick = new Joystick(Joysticks.left);
        rightStick = new Joystick(Joysticks.right);
 
        log.info("Initialization complete.");
    }

    /**
     * this method starts when operator mode is enabled.
     */
    public void teleopInit()
    {
        log.info("Entering teleoperated mode. Activating controls.");
    }

    /**
     * 
     * This function is ran in a loop during operator control.
     */
    public void teleopPeriodic() 
    {
        
        displayControl();
        
        // Use the Joystick inputs to update the drive system
        SwerveVector Mag = new SwerveVector(leftStick.getMagnitude(),
                                            leftStick.getDirectionDegrees(),
                                            true);
        
        drive.Update(Mag, rightStick.getAxis(Joystick.AxisType.kX));
        
        // Shift gears if necessary
        if(leftStick.getRawButton(2))
        {
            drive.setGear(SwerveConstants.GearLow);
        }
        else
        {
            drive.setGear(SwerveConstants.GearHigh);
        }
    }

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

    //------------------
    // Private Functions
    //-----

    private void displayControl()
    {
        //DriverStationLCD.getInstance().println(DriverStationLCD.Line.kUser1, 1,
        //        "Dist: " + String.valueOf(Math.floor(sonicSignal / 12)) + "ft. "
        //        + String.valueOf(Math.floor(sonicSignal % 12)) + "in.");

        DriverStationLCD.getInstance().updateLCD();
    }
}
