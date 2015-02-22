package com.taurus;

import com.taurus.controller.Controller;
import com.taurus.controller.ControllerChooser;
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

    // Motor Objects
    protected SwerveChassis drive;

    // Joysticks
    protected Controller controller;
    private ControllerChooser controllerChooser;

    private PowerDistributionPanel PDP;

    public static Preferences prefs;
    
    public Application()
    {
        prefs = Preferences.getInstance();
        
        drive = new SwerveChassis(controller);
        
        PDP = new PowerDistributionPanel();

        controllerChooser = new ControllerChooser();
        controller = controllerChooser.GetController();
    }
    
    public void TeleopInit()
    {
        controller = controllerChooser.GetController();
        
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
            
            SmartDashboard.putNumber("Swerve Task Length", Timer.getFPGATimestamp() - TimeLastSwerve);
        }

        TeleopPeriodicRobotSpecific();
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
//        for (int i = 0; i < 16; i++)
//        {
//            SmartDashboard.putNumber("PDP " + i, PDP.getCurrent(i));
//        }
//
//        SmartDashboard.putNumber("PDP Total Current", PDP.getTotalCurrent());
//        SmartDashboard.putNumber("PDP Total Power", PDP.getTotalPower());
//        SmartDashboard.putNumber("PDP Total Energy", PDP.getTotalEnergy());
        SmartDashboard.putNumber("Voltage", PDP.getVoltage());

        // display the joysticks on smart dashboard
//        SmartDashboard.putNumber("Left Mag",
//                controller.getMagnitude(Hand.kLeft));
//        SmartDashboard.putNumber("Left Angle",
//                controller.getDirectionDegrees(Hand.kLeft));
//        SmartDashboard.putNumber("Right Mag",
//                controller.getMagnitude(Hand.kRight));
//        SmartDashboard.putNumber("Right Angle",
//                controller.getDirectionDegrees(Hand.kRight));

//        if (driveScheme.get() == DriveScheme.ANGLE_DRIVE)
//        {
//            SmartDashboard.putNumber("Angle heading",
//                    controller.getAngleDrive_Heading());
//        }
//
//        // display each wheel's mag and angle in SmartDashboard
//        for (int i = 0; i < SwerveConstants.WheelCount; i++)
//        {
//            SmartDashboard.putNumber("Wheel " + Integer.toString(i) + " Mag",
//                    drive.getWheelActual(i).getMag());
//            SmartDashboard.putNumber("Wheel " + Integer.toString(i) + " Angle",
//                    drive.getWheelActual(i).getAngle());
//        }

        SmartDashboard.putNumber("Gyro Angle", drive.getGyro().getYaw());
        SmartDashboard.putNumber("Last Heading", drive.getLastHeading());
        SmartDashboard.putBoolean("Field Relative", drive.getFieldRelative());

        SmartDashboard.putBoolean("Brake", drive.getBrake());
    }


}
