package com.taurus.robotspecific2015;

// Manages manipulators and supporting systems
public class Lift 
{   
   PneumaticSubsystem Rails;
   PneumaticSubsystem ContainerCar;
   PneumaticSubsystem ContainerFixed;
   PneumaticSubsystem StackHolder;
   PneumaticSubsystem JawsOfLife;
   
   // Initialize lift and all objects owned by the lift
   public Lift()
   {
	   // Create all objects owned by this class
	   Rails = new PneumaticSubsystem(Constants.ChannelRail, true);
	   ContainerCar = new PneumaticSubsystem(Constants.ChannelContainerCar, false);
	   ContainerFixed = new PneumaticSubsystem(Constants.ChannelContainerFixed, false);	   
	   StackHolder = new PneumaticSubsystem(Constants.ChannelStackHolder, false);
	   JawsOfLife = new PneumaticSubsystem(Constants.ChannelJawsOfLife, false);
   }
   
   // Routine to add new tote to existing stack
   public void AddToteToStack()
   {
	   // Assumption: We have sensed that the tote is already in the robot, ready to be lifted
	   // Assumption: Pneumatics in expected starting position
	   
	   // TODO: Raise the car up until top position sensed
	   
	   // TODO: Extend the stack holder pneumatics to hold the totes up
	   
	   // TODO: Lower the car until bottom position sensed
   }
   
   // Routine to lift container to start a new stack
   public void AddContainerToStack()
   {
	   
   }
   
   public void EjectStack()
   {
	   
   }
}