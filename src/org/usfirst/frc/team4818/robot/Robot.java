package org.usfirst.frc.team4818.robot;


import com.taurus.Application;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.SampleRobot;

public class Robot extends SampleRobot {    

    private Application app;
    
    public void robotInit()
    {
        int appToRun = Preferences.getInstance().getInt("Application", 1);
        
        switch(appToRun)
        {
            case 0:
                // Run Swerve
                app = new com.taurus.swerve.Application();
                break;
            
            case 1:
                // Run 2015 specific
                app = new com.taurus.robotspecific2015.Application();
                break;
            
            case 2:
                // Run Vision test
                app = new com.taurus.robotspecific2015.VisionApplication();
                break;
                
            default:
                // Run 2015 specific
                app = new com.taurus.robotspecific2015.Application();
                break;
        }
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
