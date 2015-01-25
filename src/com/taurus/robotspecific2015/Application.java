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
    public void teleopAppInit()
    {
    	// TODO: Make sure application calls this as a template method
    }
    
    // Runs during operator control
    public void teleopAppPeriodic()
    {
    	// TODO: Make sure application calls this as a template method
    }
    
    // Called at start of autonomous mode
    public void autonomousInit()
    {
    	
    }

    // Called periodically during autonomous mode
    public void autonomousPeriodic()
    {
    	// TODO: Put the autonomous routine here
    }
    
    // Called at start of test mode
    public void testInit()
    {
    	
    }
    
    // Called periodically during test mode
    public void testPeriodic()
    {
    	int testMode = 0;
		boolean button1 = false;  // TODO: Get these button values from the controller
		boolean button2 = false;
		boolean button3 = false;
		boolean button4 = false;
		boolean button5 = false;
    	
    	// TODO: Add test modes for cylinders and motors and features.
    	switch(testMode)
    	{
    	case Constants.TEST_MODE_PNEUMATICS:    		
    		PneumaticSubsystem testCylinders;
    		
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
    		
    		// Toggle selected cylinders to opposite position
			if (testCylinders.IsExtended())
			{
				testCylinders.Contract();
			}
			else
			{
				testCylinders.Extend();
			}
    		break;
    	case Constants.TEST_MODE_CAR:
    		if (button1)
    		{
    			// TODO: Move the car up one position
    		}
    		else if (button2)
    		{
    			// TODO: Move the car down one position
    		}
    		break;
    	default:
    		// If test mode is not given, do regular mode
    		teleopPeriodic();
    		break;
    	}
    	
    }
}
