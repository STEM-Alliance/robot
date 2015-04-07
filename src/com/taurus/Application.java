package com.taurus;

import com.taurus.controller.Controller;
import com.taurus.controller.ControllerChooser;
import com.taurus.robotspecific2015.UltraSonicDrive;
import com.taurus.swerve.SwerveChassis;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class Application {
    public abstract void TeleopInitRobotSpecific();

    public abstract void TeleopPeriodicRobotSpecific();

    public abstract void TeleopDeInitRobotSpecific();

    public abstract void AutonomousInitRobotSpecific();

    public abstract void AutonomousPeriodicRobotSpecific();

    public abstract void AutonomousDeInitRobotSpecific();

    public abstract void TestModeInitRobotSpecific();

    public abstract void TestModePeriodicRobotSpecific();

    public abstract void TestModeDeInitRobotSpecific();

    public abstract void DisabledInitRobotSpecific();

    public abstract void DisabledPeriodicRobotSpecific();

    public abstract void DisabledDeInitRobotSpecific();

    private final double TIME_RATE_DASH = .2;
    private final double TIME_RATE_SWERVE = .01;
    
    private double TimeLastDash = 0;
    private double TimeLastSwerve = 0;
    private double TimeLastApp = 0;

    // Motor Objects
    protected SwerveChassis drive;

    // Joysticks
    protected Controller controller;
    private ControllerChooser controllerChooser;

    private PowerDistributionPanel PDP;

    public static Preferences prefs;
    
    public static int ROBOT_VERSION = 0;
    
    public Application()
    {
        prefs = Preferences.getInstance();
        
        ROBOT_VERSION = prefs.getInt("ROBOT_VERSION", ROBOT_VERSION);
                
        PDP = new PowerDistributionPanel();

        controllerChooser = new ControllerChooser();
        controller = controllerChooser.GetController();
        
        drive = new UltraSonicDrive(controller);
    }
    
    public void TeleopInit()
    {
        //drive.ZeroGyro();

        TeleopInitRobotSpecific();
    }

    public void TeleopPeriodic()
    {
        if ((Timer.getFPGATimestamp() - TimeLastDash) > TIME_RATE_DASH)
        {
            TimeLastDash = Timer.getFPGATimestamp();
            
            UpdateDashboard();
            
            //SmartDashboard.putNumber("Dash Task Length", Timer.getFPGATimestamp() - TimeLastDash);
        }

        if ((Timer.getFPGATimestamp() - TimeLastSwerve) > TIME_RATE_SWERVE)
        {
            TimeLastSwerve = Timer.getFPGATimestamp();
            
            drive.run();
            
            //SmartDashboard.putNumber("Swerve Task Length", Timer.getFPGATimestamp() - TimeLastSwerve);
        }

        TimeLastApp = Timer.getFPGATimestamp();
        
        TeleopPeriodicRobotSpecific();

        SmartDashboard.putNumber("App Task Length", Timer.getFPGATimestamp() - TimeLastApp);
    }

    public void TeleopDeInit()
    {
        // TODO: Put common routine here

        TeleopDeInitRobotSpecific();
    }

    /**
     * Run the normal operating Drive system
     */

    public void AutonomousInit()
    {
        // TODO: Put common routine here
        controller = controllerChooser.GetController();
        
        AutonomousInitRobotSpecific();
    }

    public void AutonomousPeriodic()
    {
        // TODO: Put common routine here

        AutonomousPeriodicRobotSpecific();
    }

    public void AutonomousDeInit()
    {
        // TODO: Put common routine here

        AutonomousDeInitRobotSpecific();
    }

    public void TestModeInit()
    {
        // TODO: Put common routine here
        controller = controllerChooser.GetController();

        TestModeInitRobotSpecific();
    }

    public void TestModePeriodic()
    {
        // TODO: Put common routine here

        TestModePeriodicRobotSpecific();
    }

    public void TestModeDeInit()
    {
        // TODO: Put common routine here

        TestModeDeInitRobotSpecific();
    }

    public void DisableInit()
    {
        // TODO: Put common routine here

        DisabledInitRobotSpecific();
    }

    public void DisablePeriodic()
    {
        // TODO: Put common routine here

        DisabledPeriodicRobotSpecific();
    }

    public void DisableDeInit()
    {
        // TODO: Put common routine here

        DisabledDeInitRobotSpecific();
    }


    /**
     * Update the dashboard with the common entries
     */
    private void UpdateDashboard()
    {
        SmartDashboard.putNumber("Voltage", PDP.getVoltage());

        SmartDashboard.putNumber("Gyro Angle", drive.getGyro().getYaw());
        SmartDashboard.putNumber("Last Heading", drive.getLastHeading());
        SmartDashboard.putBoolean("Field Relative", drive.getFieldRelative());

        SmartDashboard.putBoolean("Brake", drive.getBrake());
    }


}
