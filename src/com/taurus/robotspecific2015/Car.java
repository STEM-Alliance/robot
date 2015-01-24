package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.POSITION_CAR;

import edu.wpi.first.wpilibj.Talon;

// State machine for lift
public class Car 
{
   private POSITION_CAR currentState;
	
   private Talon MotorsCar;
	
   // Constructor
   public Car()
   {
	   MotorsCar = new Talon(Constants.MOTOR_TALON_PIN_CAR);
   }
   
   // Get the current position of the car
   public POSITION_CAR GetPosition()
   {
	   return currentState;
   }

   // Move car to top position
   public void GoToTop()
   {
	   boolean sensorAtPosition = false;  // TODO: Get this value from the sensor that we ultimately use
	   
	   if(sensorAtPosition)
	   {
		   // Stop motor and advance state machine state
		   MotorsCar.set(0);
		   currentState = Constants.POSITION_CAR.TOP;
	   }
	   else
	   {
		   MotorsCar.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_UP);
	   }	   
   }
   
   // Move car to position that pushes last tote high enough to make room to disengage stack holder
   public void GoToDeStack()
   {
	   boolean sensorAtPosition = false;  // TODO: Get this value from the sensor that we ultimately use
	   
	   if(sensorAtPosition)
	   {
		   // Stop motor and advance state machine state
		   MotorsCar.set(0);
		   currentState = Constants.POSITION_CAR.DESTACK;
	   }
	   else
	   {
		   // TODO: Set motor, specifically selecting the correct direction based on current position
	   }
   }
   
   // Move car to position that can receive totes from chute
   public void GoToChute()
   {
	   boolean sensorAtPosition = false;  // TODO: Get this value from the sensor that we ultimately use
	   
	   if(sensorAtPosition)
	   {
		   // Stop motor and advance state machine state
		   MotorsCar.set(0);
		   currentState = Constants.POSITION_CAR.CHUTE;
	   }
	   else
	   {
		   // TODO: Set motor, specifically selecting the correct direction based on current position
	   }
   }

   // Move car to bottom position
   public void GoToBottom()
   {
	   boolean sensorAtPosition = false;  // TODO: Get this value from the sensor that we ultimately use
	   
	   if(sensorAtPosition)
	   {
		   // Stop motor and advance state machine state
		   MotorsCar.set(0);
		   currentState = Constants.POSITION_CAR.BOTTOM;
	   }
	   else
	   {
		   MotorsCar.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_DOWN);
	   }
   }
}
