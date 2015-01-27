package com.taurus.robotspecific2015;

public class Application extends com.taurus.Application
{
	Lift lift;
	
	public Application()
	{
		lift = new Lift();
	}
	
   public void TeleopInitRobotSpecific()
   {
	   
   }
   
   public void TeleopPeriodicRobotSpecific()
   {
	   
   }
   
   public void TeleopDeInitRobotSpecific()
   {
	   
   }
		
   public void AutonomousInitRobotSpecific()
   {
	   
   }
   
   public void AutonomousPeriodicRobotSpecific()
   {
	   
   }
   
   public void AutonomousDeInitRobotSpecific()
   {
	   
   }
		
   public void TestModeInitRobotSpecific()
   {
	   
   }
   
   public void TestModePeriodicRobotSpecific()
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
   		break;
   	}
   }
   
   public void TestModeDeInitRobotSpecific()
   {
	   
   }
		
   public void DisabledInitRobotSpecific()
   {
	   
   }
   
   public void DisabledPeriodicRobotSpecific()
   {
	   
   }
   
   public void DisabledDeInitRobotSpecific()
   {
	   
   }
}
