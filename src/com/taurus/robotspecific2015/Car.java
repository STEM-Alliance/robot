package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.POSITION_CAR;

import edu.wpi.first.wpilibj.Talon;

// State machine for lift
public class Car 
{
   private POSITION_CAR currentState;
	
   public Talon MotorsCar = new Talon(Constants.MOTOR_TALON_PIN_CAR);
	
   // Constructor
   public Car()
   {
	   // TODO: Move the car to the bottom as a 'home' position, sense that we are there, then move the car to the chute desired starting position 
   }

   // Move car to where the new tote will be held in place by the stack holder
   public boolean GoToStack()
   {
	   boolean sensorAtPosition = false;  // TODO: Get this value from the sensor that we ultimately use
	   
	   if(sensorAtPosition || currentState == Constants.POSITION_CAR.STACK)
	   {
		   // Stop motor and advance state machine state
		   MotorsCar.set(0);
		   currentState = Constants.POSITION_CAR.STACK;
	   }
	   else
	   {
		   MotorsCar.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_UP);
	   }
	   
	   // Is the car at the stack position or not?
	   return currentState == Constants.POSITION_CAR.STACK;
   }
   
   // Move car to position that pushes last tote high enough to make room to disengage stack holder
   public boolean GoToDeStack()
   {
	   boolean sensorAtPosition = false;  // TODO: Get this value from the sensor that we ultimately use
	   
	   if(sensorAtPosition || currentState == Constants.POSITION_CAR.DESTACK)
	   {
		   // Stop motor and advance state machine state
		   MotorsCar.set(0);
		   currentState = Constants.POSITION_CAR.DESTACK;
	   }
	   else
	   {
		   // Set motor, specifically selecting the correct direction based on current position
		   if (currentState == Constants.POSITION_CAR.STACK)
		   {
			   MotorsCar.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_DOWN);
		   }
		   else
		   {
			   MotorsCar.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_UP);
		   }
	   }
	   
	    // Is the car at the destack position or not?
	   return currentState == Constants.POSITION_CAR.DESTACK;
   }
   
   // Move car to position that can receive totes from chute
   public boolean GoToChute()
   {
	   boolean sensorAtPosition = false;  // TODO: Get this value from the sensor that we ultimately use
	   
	   if(sensorAtPosition || currentState == Constants.POSITION_CAR.CHUTE)
	   {
		   // Stop motor and advance state machine state
		   MotorsCar.set(0);
		   currentState = Constants.POSITION_CAR.CHUTE;
	   }
	   else
	   {
		   // Set motor, specifically selecting the correct direction based on current position
		   if (currentState == Constants.POSITION_CAR.BOTTOM)
		   {
			   MotorsCar.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_UP);
		   }
		   else
		   {
			   MotorsCar.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_DOWN);
		   }
	   }
	   
	   // Is the car at the chute position or not?
	   return currentState == Constants.POSITION_CAR.CHUTE;
   }

   // Move car to bottom position
   public boolean GoToBottom()
   {
	   boolean sensorAtPosition = false;  // TODO: Get this value from the sensor that we ultimately use
	   
	   if(sensorAtPosition || currentState == Constants.POSITION_CAR.BOTTOM)
	   {
		   // Stop motor and advance state machine state
		   MotorsCar.set(0);
		   currentState = Constants.POSITION_CAR.BOTTOM;
	   }
	   else
	   {
         MotorsCar.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_DOWN);
      }
	   
	// Is the car at the bottom position or not?
	   return currentState == Constants.POSITION_CAR.BOTTOM;
   }
}
