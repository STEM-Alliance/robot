package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.POSITION_CAR;

import edu.wpi.first.wpilibj.Talon;

// State machine for lift
public class Car 
{
   private POSITION_CAR CurrentState;
	
   Talon Motors = new Talon(Constants.MOTOR_TALON_PIN_CAR);  // TODO: This is actually two motors. Create the second one
   Sensor SensorStack = new SensorDigital();
   Sensor SensorDestack = new SensorDigital();
   Sensor SensorChute = new SensorDigital();
   Sensor SensorBottom = new SensorDigital();
	
   // Constructor
   public Car()
   {
	   // TODO: Move the car to the bottom as a 'home' position, sense that we are there, then move the car to the chute desired starting position 
   }
   
   public POSITION_CAR GetPosition()
   {
      return CurrentState;
   }

   // Move car to where the new tote will be held in place by the stack holder
   public boolean GoToStack()
   {
	   if(SensorStack.IsOn() || CurrentState == Constants.POSITION_CAR.STACK)
	   {
		   // Stop motor and advance state machine state
		   Motors.set(0);
		   CurrentState = Constants.POSITION_CAR.STACK;
	   }
	   else
	   {
		   Motors.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_FORWARD);
	   }
	   
	   // Is the car at the stack position or not?
	   return CurrentState == Constants.POSITION_CAR.STACK;
   }
   
   // Move car to position that pushes last tote high enough to make room to disengage stack holder
   public boolean GoToDestack()
   {
	   if(SensorDestack.IsOn() || CurrentState == Constants.POSITION_CAR.DESTACK)
	   {
		   // Stop motor and advance state machine state
		   Motors.set(0);
		   CurrentState = Constants.POSITION_CAR.DESTACK;
	   }
	   else
	   {
		   // Set motor, specifically selecting the correct direction based on current position
		   if (CurrentState == Constants.POSITION_CAR.STACK)
		   {
			   Motors.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_BACKWARD);
		   }
		   else
		   {
			   Motors.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_FORWARD);
		   }
	   }
	   
	    // Is the car at the destack position or not?
	   return CurrentState == Constants.POSITION_CAR.DESTACK;
   }
   
   // Move car to position that can receive totes from chute
   public boolean GoToChute()
   {
      if(SensorChute.IsOn() || CurrentState == Constants.POSITION_CAR.CHUTE)
	   {
		   // Stop motor and advance state machine state
		   Motors.set(0);
		   CurrentState = Constants.POSITION_CAR.CHUTE;
	   }
	   else
	   {
		   // Set motor, specifically selecting the correct direction based on current position
		   if (CurrentState == Constants.POSITION_CAR.BOTTOM)
		   {
			   Motors.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_FORWARD);
		   }
		   else
		   {
			   Motors.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_BACKWARD);
		   }
	   }
	   
	   // Is the car at the chute position or not?
	   return CurrentState == Constants.POSITION_CAR.CHUTE;
   }

   // Move car to bottom position
   public boolean GoToBottom()
   {
      if(SensorBottom.IsOn() || CurrentState == Constants.POSITION_CAR.BOTTOM)
	   {
		   // Stop motor and advance state machine state
		   Motors.set(0);
		   CurrentState = Constants.POSITION_CAR.BOTTOM;
	   }
	   else
	   {
         Motors.set(Constants.MOTOR_SPEED_CAR * Constants.MOTOR_DIRECTION_BACKWARD);
      }
	   
	// Is the car at the bottom position or not?
	   return CurrentState == Constants.POSITION_CAR.BOTTOM;
   }
}
