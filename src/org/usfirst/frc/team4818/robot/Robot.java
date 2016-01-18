package org.usfirst.frc.team4818.robot;


import com.taurus.Application;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends SampleRobot {

    private Application app;
    private int appIndex = -1;
    
    public void robotInit()
    {
        GetApp();
    }
    
    private void GetApp()
    {
        int appIndexNew = Preferences.getInstance().getInt("Application", 1);
        
        if(appIndex != appIndexNew || appIndex == -1)
        {
            switch(appIndexNew)
            {
                case 0:
                    // Run Swerve
                    app = new com.taurus.swerve.Application();
                    SmartDashboard.putString("Application", "Swerve");
                    break;
                    
                case 1:
                    // Run RockerApplication
                    app = new com.taurus.rocker.RockerApplication();
                    SmartDashboard.putString("Application", "RockerApplication");
                    break;
                    
                    
                default:
                    // Run Swerve
                    app = new com.taurus.swerve.Application();
                    SmartDashboard.putString("Application", "Swerve");
                    break;
            }
            
            appIndex = appIndexNew;
        }
    }

    public void operatorControl()
    {
        GetApp();
        
        app.TeleopInit();
        
        while (isOperatorControl() && isEnabled())
        {
            app.TeleopPeriodic();
        }
        
        app.TeleopDeInit();
    }
    
    
    public void autonomous()
    {
        GetApp();
        
        app.AutonomousInit();
        
        while (isAutonomous() && isEnabled())
        {
            app.AutonomousPeriodic();
        }
        
        app.AutonomousDeInit();
    }
    
    public void disabled()
    {
        app.DisabledInit();
        
        while (isDisabled())
        {
            app.DisabledPeriodic();
        }
        
        app.DisabledInit();
    }
    
    public void test()
    {
        GetApp();
        
        app.TestModeInit();
        
        while (isTest() && isEnabled())
        {
            app.TestModePeriodic();
        }
        
        app.TestModeDeInit();
    }
}
