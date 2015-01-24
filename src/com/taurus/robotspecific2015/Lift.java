package com.taurus.robotspecific2015;

// Manages manipulators and supporting systems
public class Lift 
{   
   Car Car = new Car();
	
   PneumaticSubsystem CylindersRails;
   PneumaticSubsystem CylindersContainerCar;
   PneumaticSubsystem CylindersContainerFixed;
   PneumaticSubsystem CylindersStackHolder;
   PneumaticSubsystem CylindersJawsOfLife;
   
   // Initialize lift and all objects owned by the lift
   public Lift()
   {
	   CylindersRails = new PneumaticSubsystem(Constants.CHANNEL_RAIL, true);
	   CylindersContainerCar = new PneumaticSubsystem(Constants.CHANNEL_CONTAINER_CAR, false);
	   CylindersContainerFixed = new PneumaticSubsystem(Constants.CHANNEL_CONTAINER_FIXED, false);	   
	   CylindersStackHolder = new PneumaticSubsystem(Constants.CHANNEL_STACK_HOLDER, false);
	   CylindersJawsOfLife = new PneumaticSubsystem(Constants.CHANNEL_JAWS_OF_LIFE, false);
   }
   
   // Routine to add new tote to existing stack
   public void AddToteToStack()
   {
	   // TODO: Turn this into a state machine
	   Car.GoToTop();
       Car.GoToChute();
   }
   
   // Routine to lift container to start a new stack
   public void AddContainerToStack()
   {
	   // TODO: Turn this into a state machine
	   CylindersRails.Contract();
	   // TODO: Sense container OR Remove contracting the rails and add that to it's own method
	   CylindersContainerCar.Extend();
	   Car.GoToTop();
	   CylindersContainerFixed.Extend();
	   CylindersContainerCar.Contract();
       Car.GoToChute();
   }
   
   public void EjectStack()
   {
	   // TODO: Turn this into a state machine
	   // TODO: Destack, let totes down, push them out, retract mechanism, reset stack holder
	   Car.GoToDeStack();
	   CylindersStackHolder.Contract();
	   Car.GoToBottom();
	   // TODO: Push the stack out
	   // TODO: Contract the stack pusher
	   CylindersStackHolder.Extend();
   }
}