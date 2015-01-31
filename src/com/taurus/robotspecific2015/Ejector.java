package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.*;

import edu.wpi.first.wpilibj.Talon;

public class Ejector
{
   STATE_EJECT StateEject = STATE_EJECT.PUSHER_EXTEND;
   POSITION_EJECTOR PositionEjector = POSITION_EJECTOR.IN;
   
   Talon Motors = new Talon(Constants.MOTOR_TALON_PIN_EJECTOR);  // TODO: This is actually two motors. Create the second one
   PneumaticSubsystem CylindersStop;
   PneumaticSubsystem CylindersPusher;
   Sensor OutSensor = null; //TODO create the specific sensor type
   Sensor InSensor = null; //TODO create the specific sensor type
   
   public Ejector()
   {
      CylindersStop = new PneumaticSubsystem(Constants.CHANNEL_STOP, Constants.TIME_EXTEND_STOP, Constants.TIME_CONTRACT_STOP, false);
      CylindersPusher = new PneumaticSubsystem(Constants.CHANNEL_PUSHER, Constants.TIME_EXTEND_PUSHER, Constants.TIME_CONTRACT_PUSHER, false);
      
      // Move the Ejector to its initial "in" position
      MoveIn();
   }
   
   public void EjectStack()
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
               StateEject = STATE_EJECT.MOVE_IN;
            }
            break;
         case MOVE_IN:
            if (MoveIn())
            {
               StateEject = STATE_EJECT.PUSHER_CONTRACT;
            }
            break;
         case PUSHER_CONTRACT:
            if (CylindersPusher.Contract())
            {
               StateEject = STATE_EJECT.PUSHER_EXTEND;
            }
            break;
         default:
            // TODO: Put error condition here
            break;
      }
   }
   
   public void StopOut()
   {
      //Extend the MotorEjector
      //Extend Stop Pneumatics
   }
   
   public void StopIn()
   {
      //Contract the MotorEjector
      //Contract Stop Pnuematics
   }
   
   private boolean MoveOut()
   {
      if(OutSensor.IsOn() || PositionEjector == Constants.POSITION_EJECTOR.OUT)
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
   
   private boolean MoveIn()
   {
      if(InSensor.IsOn() || PositionEjector == Constants.POSITION_EJECTOR.IN)
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