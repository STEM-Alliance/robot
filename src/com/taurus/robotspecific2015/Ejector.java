package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.*;

import edu.wpi.first.wpilibj.Talon;

public class Ejector
{
   STATE_EJECT StateEject = STATE_EJECT.PUSHER_EXTEND;
   POSITION_EJECTOR PositionEjector = POSITION_EJECTOR.IN;

   Talon Motors = new Talon(Constants.MOTOR_TALON_PIN_EJECTOR); // TODO: This is actually two motors. Create the second one
   PneumaticSubsystem CylindersStop;
   PneumaticSubsystem CylindersPusher;
   Sensor OutSensor = new SensorDigital();
   Sensor InSensor = new SensorDigital();

   public Ejector()
   {
      CylindersStop = new PneumaticSubsystem(Constants.CHANNEL_STOP, Constants.TIME_EXTEND_STOP, Constants.TIME_CONTRACT_STOP, false);
      CylindersPusher = new PneumaticSubsystem(Constants.CHANNEL_PUSHER, Constants.TIME_EXTEND_PUSHER, Constants.TIME_CONTRACT_PUSHER, false);

      // Move the Ejector to its initial "in" position
      MoveIn();
   }

   public boolean EjectStack()
   {
      switch (StateEject)
      {
         case PUSHER_EXTEND:
            if (CylindersPusher.Extend())
            {
               StateEject = STATE_EJECT.MOVE_OUT;
            }
            break;
         case MOVE_OUT:
            if (MoveOut())
            {
               StateEject = STATE_EJECT.RESET;
            }
            break;
         // IMPORTANT: Resetting the Ejector needs to happen, but with a seperate method call
         //            This allows robot to asynchronous drive and reset the Ejector
         default:
            // TODO: Put error condition here
            break;
      }

      // Did we just finish ejecting the stack?
      return StateEject == STATE_EJECT.RESET;
   }

   // Asynchronously finish EjectStack(), which can be called while driving
   public boolean ResetEjectStack()
   {
      boolean finishedReset = false;
      
      if (StateEject == STATE_EJECT.RESET)
      {
         // IMPORTANT: Use single '&' to execute all cleanup routines asynchronously
         if (MoveIn() & CylindersPusher.Contract())
         {
            finishedReset = true;
            StateEject = STATE_EJECT.PUSHER_EXTEND;
         }
      }      
      return finishedReset;
   }

   // Disengage the stop for stopping totes from the chute
   public boolean StopOut()
   {
      boolean done = false;

      if (CylindersStop.Extend() && MoveOut())
      {
         done = true;
      }
      return done;
   }

   // Engage the stop for stopping totes from the chute
   public boolean StopIn()
   {
      boolean done = false;

      if (CylindersStop.Contract() && MoveIn())
      {
         done = true;
      }
      return done;
   }

   // Move the Ejector outward with the motors
   private boolean MoveOut()
   {
      if (OutSensor.IsOn() || PositionEjector == Constants.POSITION_EJECTOR.OUT)
      {
         // Stop motor and advance state machine state
         Motors.set(0);
         PositionEjector = Constants.POSITION_EJECTOR.OUT;
      }
      else
      {
         Motors.set(Constants.MOTOR_SPEED_EJECTOR * Constants.MOTOR_DIRECTION_FORWARD);
      }

      // Is the Ejector in the out position or not?
      return PositionEjector == Constants.POSITION_EJECTOR.OUT;
   }

   // Move the Ejector inward with the motors
   private boolean MoveIn()
   {
      if (InSensor.IsOn() || PositionEjector == Constants.POSITION_EJECTOR.IN)
      {
         // Stop motor and advance state machine state
         Motors.set(0);
         PositionEjector = Constants.POSITION_EJECTOR.IN;
      }
      else
      {
         Motors.set(Constants.MOTOR_SPEED_EJECTOR * Constants.MOTOR_DIRECTION_BACKWARD);
      }

      // Is the Ejector in the in position or not?
      return PositionEjector == Constants.POSITION_EJECTOR.IN;
   }
}