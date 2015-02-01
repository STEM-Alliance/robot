package org.usfirst.frc.team4818.robot;


import com.taurus.Application;

import edu.wpi.first.wpilibj.SampleRobot;



/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends SampleRobot {    

    private Application app;
    
    public void robotInit()
    {
        app = new com.taurus.swerve.Application();
    }

    public void operatorControl()
    {
        app.TeleopInit();
        
        while (isOperatorControl() && isEnabled())
        {
            app.TeleopPeriodic();
        }
        
        app.TeleopDeInit();
    }
    
    
    public void autonomous()
    {
        app.AutonomousInit();
        
        while (isAutonomous() && isEnabled())
        {
            app.AutonomousPeriodic();
        }
        
        app.AutonomousDeInit();
    }
    
    public void disabled()
    {
        app.DisableInit();
        
        while (isDisabled())
        {
            app.DisablePeriodic();
        }
        
        app.DisableDeInit();
    }
    
    public void test()
    {
        app.TestModeInit();
        
        while (isTest() && isEnabled())
        {
            app.TestModePeriodic();
        }
        
        app.TestModeDeInit();
    }
}
