package com.taurus.robotspecific2015;

public class Application extends com.taurus.Application
{
   Lift lift;
   Car TestModeCar;
   Ejector TestModeEjector;
   Sensor TestModeToteIntakeSensor;

   public Application()
   {
      super.Application();  // Initialize anything in the super class constructor
      
      lift = new Lift();
   }

   public void TeleopInitRobotSpecific()
   {

   }

   public void TeleopPeriodicRobotSpecific()
   {
      boolean button1 = false; // TODO: Get these button values from the controller
      boolean button2 = false;
      boolean button3 = false;
      boolean button4 = false; // TODO: Do we want a button to cancel the current routine
      
      // TODO: Make sure that we stay in the same mode after the button 
      //       is pressed until we are done doing that routine 
      
      if (button1)
      {
         lift.AddChuteToteToStack();
      }
      else if (button2)
      {
         //add container
         lift.AddContainerToStack();
      }
      else if (button3)
      {
         lift.EjectStack();
      }
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
      TestModeCar = lift.LiftCar;
      TestModeEjector = lift.StackEjector;
      TestModeToteIntakeSensor = lift.ToteIntakeSensor;
   }

   public void TestModePeriodicRobotSpecific()
   {
      int testMode = 0;
      boolean button1 = false; // TODO: Get these button values from the controller
      boolean button2 = false;
      boolean button3 = false;
      boolean button4 = false;
      boolean button5 = false;

      // TODO: Add test modes for cylinders and motors and features.
      switch (testMode)
      {
         case Constants.TEST_MODE_PNEUMATIC:
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
               TestModeCar.Motors.Set(Constants.MOTOR_SPEED_CAR1 * Constants.MOTOR_DIRECTION_FORWARD);
            }
            else if (button2)
            {
               TestModeCar.Motors.Set(Constants.MOTOR_SPEED_CAR1 * Constants.MOTOR_DIRECTION_BACKWARD);
            }
            else if (button3)
            {
               TestModeEjector.Motors.set(Constants.SCALING_EJECTOR * Constants.MOTOR_DIRECTION_FORWARD);
            }
            else if (button4)
            {
               TestModeEjector.Motors.set(Constants.SCALING_EJECTOR * Constants.MOTOR_DIRECTION_BACKWARD);
            }
            break;
         default:
            break;
      }
      // TODO: Get the value of one sensor and report that somehow (Smartdashboard, print, etc)
      if (TestModeToteIntakeSensor.IsOn())
      {
         // TODO: Update smartdashboard or however we show sensors
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
