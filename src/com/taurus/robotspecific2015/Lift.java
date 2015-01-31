package com.taurus.robotspecific2015;

import com.taurus.robotspecific2015.Constants.*;

import edu.wpi.first.wpilibj.Timer;

// Manages manipulators and supporting systems
public class Lift
{
   // TODO add container state variable
   boolean ContainerInStack = false;
   int TotesInStack = 0;
   double TimeStart;
   double TimeCurrent;
   STATE_ADD_TOTE_TO_STACK StateAddToteToStack = STATE_ADD_TOTE_TO_STACK.INTAKE_TOTE;
   STATE_ADD_CONTAINER_TO_STACK StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.RAILS_UP;
   STATE_EJECT_STACK StateEjectStack = STATE_EJECT_STACK.JAWS_EXTEND;

   Car LiftCar = new Car(); // TODO Name it something different than car
   PneumaticSubsystem CylindersRails;
   PneumaticSubsystem CylindersContainerCar;
   PneumaticSubsystem CylindersContainerFixed;
   PneumaticSubsystem CylindersStackHolder;
   PneumaticSubsystem CylindersJawsOfLife;
   Sensor ToteIntakeSensor = null; // TODO create the specific sensor type

   // Initialize lift and all objects owned by the lift
   public Lift()
   {
      CylindersRails = new PneumaticSubsystem(Constants.CHANNEL_RAIL, Constants.TIME_EXTEND_RAILS, Constants.TIME_CONTRACT_RAILS, true);
      CylindersContainerCar = new PneumaticSubsystem(Constants.CHANNEL_CONTAINER_CAR, Constants.TIME_EXTEND_CONTAINER_CAR, Constants.TIME_CONTRACT_CONTAINER_CAR, false);
      CylindersContainerFixed = new PneumaticSubsystem(Constants.CHANNEL_CONTAINER_FIXED, Constants.TIME_EXTEND_CONTAINER_FIXED, Constants.TIME_CONTRACT_CONTAINER_FIXED, false);
      CylindersStackHolder = new PneumaticSubsystem(Constants.CHANNEL_STACK_HOLDER, Constants.TIME_EXTEND_STACK_HOLDER, Constants.TIME_CONTRACT_STACK_HOLDER, false);
      CylindersJawsOfLife = new PneumaticSubsystem(Constants.CHANNEL_JAWS_OF_LIFE, Constants.TIME_EXTEND_JAWS, Constants.TIME_CONTRACT_JAWS, false);
   }

   // Routine to add a new tote to existing stack
   public boolean AddToteToStack()
   {
      if (TotesInStack < 5)
      {
         switch (StateAddToteToStack)
         {
            case INTAKE_TOTE:
               // When sensor triggered, go to next state to lift the tote
               if (ToteIntakeSensor.IsOn())
               {
                  StateAddToteToStack = STATE_ADD_TOTE_TO_STACK.LIFT_TOTE;
               }
               break;
            case LIFT_TOTE:
               if (LiftCar.GoToStack())
               {
                  StateAddToteToStack = STATE_ADD_TOTE_TO_STACK.HANDLE_CONTAINER;
               }
               break;
            case HANDLE_CONTAINER:
               if (TotesInStack == 0)
               {
                  if (CylindersContainerFixed.Contract())
                  {
                     StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.LOWER_CAR;
                  }
               }
               else if (TotesInStack == 4)
               {
                  if (CylindersJawsOfLife.Contract())
                  {
                     StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.LOWER_CAR;
                  }
               }
               else
               {
                  StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.LOWER_CAR;
               }
               break;
            case LOWER_CAR:
               if (LiftCar.GoToChute())
               {
                  StateAddToteToStack = STATE_ADD_TOTE_TO_STACK.INTAKE_TOTE;
                  TotesInStack = TotesInStack + 1;
               }
               break;
            default:
               // TODO: Put error condition here
               break;
         }
      }

      // If the sensor triggered, and we have 5 totes, we have six totes and this method is "done"
      return TotesInStack == 5 && ToteIntakeSensor.IsOn();
   }

   // Routine to lift a container to start a new stack
   public boolean AddContainerToStack()
   {
      if (TotesInStack == 0 && ContainerInStack == false)
      {
         switch (StateAddContainerToStack)
         {
            case RAILS_UP:
               if (CylindersRails.Contract())
               {
                  StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.CONTAINER_CAR_EXTEND;
               }
               break;
            case CONTAINER_CAR_EXTEND:
               if (CylindersContainerCar.Extend())
               {
                  StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.LIFT_CAR;
               }
               break;
            case LIFT_CAR:
               if (LiftCar.GoToStack()) // TODO: Add new height for adding container to stack?
               {
                  StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.CONTAINER_FIXED_EXTEND;
               }
               break;
            case CONTAINER_FIXED_EXTEND:
               if (CylindersContainerFixed.Extend())
               {
                  StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.CONTAINER_CAR_CONTRACT;
               }
               break;
            case CONTAINER_CAR_CONTRACT:
               if (CylindersContainerCar.Contract())
               {
                  StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.LOWER_CAR;
               }
               break;
            case LOWER_CAR:
               if (LiftCar.GoToChute())
               {
                  StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.RAILS_EXTEND;
               }
               break;
            case RAILS_EXTEND:
               if (CylindersRails.Extend())
               {
                  ContainerInStack = true;
                  StateAddContainerToStack = STATE_ADD_CONTAINER_TO_STACK.RAILS_UP;
               }
               break;
            default:
               // TODO: Put error condition here
               break;
         }
      }
      return ContainerInStack;
   }

   // Place the stack on the ground, then push it onto the scoring platform
   public void EjectStack()
   {
      if (true)
      {
         switch (StateEjectStack)
         {
            case JAWS_EXTEND:
               if (CylindersJawsOfLife.Extend())
               {
                  StateEjectStack = STATE_EJECT_STACK.LIFT_CAR;
               }
               break;
            case LIFT_CAR:
               if (LiftCar.GoToDeStack()) // TODO: Add new height for adding container to stack?
               {
                  StateEjectStack = STATE_EJECT_STACK.STACK_HOLDER_CONTRACT;
               }
               break;
            case STACK_HOLDER_CONTRACT:
               if (CylindersStackHolder.Contract())
               {
                  StateEjectStack = STATE_EJECT_STACK.LOWER_CAR;
               }
               break;
            case LOWER_CAR:
               if (LiftCar.GoToChute())
               {
                  StateEjectStack = STATE_EJECT_STACK.STACK_HOLDER_EXTEND;
               }
               break;
            // TODO: Push the stack out
            // TODO: Contract the stack pusher
            case STACK_HOLDER_EXTEND:
               if (CylindersStackHolder.Extend())
               {
                  ContainerInStack = false;
                  TotesInStack = 0;
                  StateEjectStack = STATE_EJECT_STACK.JAWS_EXTEND;
               }
               break;
            default:
               // TODO: Put error condition here
               break;
         }
      }

      // // TODO: Turn this into a state machine
      // CylindersJawsOfLife.Extend();
      // LiftCar.GoToDeStack();
      // CylindersStackHolder.Contract();
      // LiftCar.GoToChute();
      // // TODO: Push the stack out
      // // TODO: Contract the stack pusher
      // CylindersStackHolder.Extend();
      //
      // TotesInStack = 0;
   }
}