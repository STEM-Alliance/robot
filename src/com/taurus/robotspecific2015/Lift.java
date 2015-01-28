package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.*;

import edu.wpi.first.wpilibj.Timer;

// Manages manipulators and supporting systems
public class Lift
{
   Car Car = new Car();
   PneumaticSubsystem CylindersRails;
   PneumaticSubsystem CylindersContainerCar;
   PneumaticSubsystem CylindersContainerFixed;
   PneumaticSubsystem CylindersStackHolder;
   PneumaticSubsystem CylindersJawsOfLife;

   STATE_ADD_TOTE_TO_STACK StateAddToteToStack = STATE_ADD_TOTE_TO_STACK.LIFT_TOTE;
   int TotesInStack = 0;
   double TimeStart;
   double TimeCurrent;

   // Initialize lift and all objects owned by the lift
   public Lift()
   {
      CylindersRails = new PneumaticSubsystem(Constants.CHANNEL_RAIL, true);
      CylindersContainerCar = new PneumaticSubsystem(Constants.CHANNEL_CONTAINER_CAR, false);
      CylindersContainerFixed = new PneumaticSubsystem(Constants.CHANNEL_CONTAINER_FIXED, false);
      CylindersStackHolder = new PneumaticSubsystem(Constants.CHANNEL_STACK_HOLDER, false);
      CylindersJawsOfLife = new PneumaticSubsystem(Constants.CHANNEL_JAWS_OF_LIFE, false);
   }

   // Routine to add a new tote to existing stack
   public void AddToteToStack()
   {
      if (TotesInStack == 5)
      {
         switch (StateAddToteToStack)
         {
            case LIFT_TOTE:
               if (Car.GoToStack())
               {
                  StateAddToteToStack = STATE_ADD_TOTE_TO_STACK.JAWS_CONTRACT;
               }
               break;
            case JAWS_CONTRACT:
               if (TotesInStack == 4)
               {
                  // Close the Jaws of Life, then wait for them to finish
                  TimeStart = Timer.getFPGATimestamp();
                  CylindersJawsOfLife.Contract();
                  StateAddToteToStack = STATE_ADD_TOTE_TO_STACK.JAWS_FINISH;
               }
               else
               {
                  // Skip the Jaws of Life, we don't have a complete stack yet
                  StateAddToteToStack = STATE_ADD_TOTE_TO_STACK.LOWER_CAR;
               }
               break;
            case JAWS_FINISH:
               // Are we done?
               double time_difference = Timer.getFPGATimestamp() - TimeStart;

               if (time_difference > Constants.TIME_CLOSE_JAWS)
               {
                  StateAddToteToStack = STATE_ADD_TOTE_TO_STACK.LOWER_CAR;
               }
               break;
            case LOWER_CAR:

               if (Car.GoToChute())
               {
                  TotesInStack = TotesInStack + 1;
               }
               break;
            default:
               // TODO: Put error condition here
               break;
         }
      }
   }

   // Routine to lift a container to start a new stack
   public void AddContainerToStack()
   {
      // TODO: Turn this into a state machine
      CylindersRails.Contract();
      // TODO: Sense container OR Remove contracting the rails and add that to it's own method
      CylindersContainerCar.Extend();
      Car.GoToStack();
      CylindersContainerFixed.Extend();
      CylindersContainerCar.Contract();
      Car.GoToChute();
   }

   // Place the stack on the ground, then push it onto the scoring platform
   public void EjectStack()
   {
      // TODO: Turn this into a state machine
      CylindersJawsOfLife.Extend();
      Car.GoToDeStack();
      CylindersStackHolder.Contract();
      Car.GoToBottom();
      // TODO: Push the stack out
      // TODO: Contract the stack pusher
      CylindersStackHolder.Extend();

      TotesInStack = 0;
   }
}