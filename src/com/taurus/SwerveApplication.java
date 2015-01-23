package com.taurus;

import com.taurus.controller.ControllerChooser;
import com.taurus.controller.ControllerSwerve;

// Swerve drive application
public class SwerveApplication implements IApplication 
{
	// Motor Objects
	public SwerveChassis drive;
    public DriveScheme driveScheme;
	
	// Joysticks
	public ControllerSwerve controller;
    public ControllerChooser controllerChooser;
	
	public SwerveApplication()
	{
		drive = new SwerveChassis();
		driveScheme = new DriveScheme();
		
        controllerChooser = new ControllerChooser();
        controller = controllerChooser.GetController();
	}
	
    // Runs when robot is disabled
    public void disabledPeriodic()
    {
    	
    }
    
    // Runs when robot is disabled
    public void disabledInit()
    {
    	
    }
    
    // Runs when operator mode is enabled.
    public void teleopInit()
    {
    	controllerChooser = new ControllerChooser();
        controller = controllerChooser.GetController();
        drive.Gyro.zeroYaw();
    }
    
    // Runs during operator control
    public void teleopPeriodic()
    {
    	// Use the Joystick inputs to update the drive system
        switch(driveScheme.get())
        {
            case DriveScheme.ANGLE_DRIVE:
            	drive.UpdateAngleDrive(controller.getAngleDrive_Velocity(), controller.getAngleDrive_Heading());
                break;
                
            default:
            case DriveScheme.HALO_DRIVE:
            	drive.UpdateHaloDrive(controller.getHaloDrive_Velocity(), controller.getHaloDrive_Rotation());
                break;
        }

        // if the button is not held down, we're in high gear
        drive.setGearHigh(controller.getHighGearEnable());
        drive.setBrake(controller.getBrake());
        if(controller.resetGyro()){
        	drive.Gyro.zeroYaw();
        }
    }
    
    // Called at start of autonomous mode
    public void autonomousInit()
    {
    	
    }

    // Called periodically during autonomous mode
    public void autonomousPeriodic()
    {
    
    }
    
    // Called at start of test mode
    public void testInit()
    {
    	
    }
    
    // Called periodically during test mode
    public void testPeriodic()
    {
    	
    }
}
