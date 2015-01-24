package com.taurus.robotspecific2015;

import com.taurus.swerve.SwerveApplication;

public class Application extends SwerveApplication
{
	Lift lift;
	
	public Application()
	{
		// TODO: Make sure parent constructor is being called
		
		lift = new Lift();
	}
	
    // Runs when operator mode is enabled.
    public void teleopInit()
    {
    	super.teleopInit(); // Call parent class init
    }
    
    // Runs during operator control
    public void teleopPeriodic()
    {
    	super.teleopPeriodic(); // Call parent class periodic (drive routine)
    }
    
    // Called at start of autonomous mode
    public void autonomousInit()
    {
    	
    }

    // Called periodically during autonomous mode
    public void autonomousPeriodic()
    {
    	// TODO: Move the robot
    }
    
    // Called at start of test mode
    public void testInit()
    {
    	
    }
    
    // Called periodically during test mode
    public void testPeriodic()
    {
    	int testMode = 0;
    	
    	// TODO: Add test modes for cylinders and motors and features.
    	switch(testMode)
    	{
    	case Constants.TEST_MODE_PNEUMATICS:    		
    		PneumaticSubsystem testCylinders;
    		boolean button1 = false;  // TODO: Get these button values from the controller
    		boolean button2 = false;
    		boolean button3 = false;
    		boolean button4 = false;
    		boolean button5 = false;
    		
    		if (button1)
    		{
    			testCylinders = lift.CylindersRails;

    		}
    		else if (button2)
    		{
    			testCylinders = lift.CylindersContainerCar;
    		}
    		else if (button3)
    		{
    			testCylinders = lift.CylindersContainerFixed;
    		}
    		else if (button4)
    		{
    			testCylinders = lift.CylindersStackHolder;
    		}
    		else if (button5)
    		{
    			testCylinders = lift.CylindersJawsOfLife;
    		}
    		else
    		{
    			testCylinders = lift.CylindersRails;
    		}
    		
    		// Toggle those cylinders
			if (testCylinders.IsExtended())
			{
				testCylinders.Contract();
			}
			else
			{
				testCylinders.Extend();
			}
    		break;
    	case Constants.TEST_MODE_MOTORS:
    		// TODO: Get controller input to determine which motors to test
    		break;
    	default:
    		// If not test mode is given, do regular mode
    		teleopPeriodic();
    		break;
    	}
    	
    }
}
