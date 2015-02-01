package com.taurus.robotspecific2015;

public abstract class Constants 
{
   // Pneumatics
   public static final byte MODULE_ID_PCU = 9;
   
   public static final int CHANNEL_RAIL = 0;
   public static final int CHANNEL_CONTAINER_CAR = 1;
   public static final int CHANNEL_CONTAINER_FIXED = 2;
   public static final int CHANNEL_STACK_HOLDER = 3;
   public static final int CHANNEL_JAWS_OF_LIFE = 4;
   public static final int CHANNEL_STOP = 5;
   public static final int CHANNEL_PUSHER = 6;   
   
   public static final double TIME_EXTEND_RAILS = 5; //TODO - what is the amount of time required to close the rails
   public static final double TIME_EXTEND_CONTAINER_CAR = 5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_EXTEND_CONTAINER_FIXED = 5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_EXTEND_STACK_HOLDER = 5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_EXTEND_JAWS = 5; //TODO - what is the amount of time required to close the jaws
   public static final double TIME_EXTEND_STOP = 5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_EXTEND_PUSHER = 5; //TODO - what is the amount of time required to close the jaws
   
   public static final double TIME_CONTRACT_RAILS = 5; //TODO - what is the amount of time required to close the rails
   public static final double TIME_CONTRACT_CONTAINER_CAR = 5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_CONTRACT_CONTAINER_FIXED = 5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_CONTRACT_STACK_HOLDER = 5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_CONTRACT_JAWS = 5; //TODO - what is the amount of time required to close the jaws
   public static final double TIME_CONTRACT_STOP = 5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_CONTRACT_PUSHER = 5; //TODO - what is the amount of time required to close the jaws
   
   // Motors
   public static final int[] PINS_MOTOR = {999, 999}; //TODO - Which PWM is this talon connected to?
   public static final int[] PINS_EJECTOR = {999}; //TODO - Which PWM is this talon connected to?
   
   public static final double[] SCALING_MOTOR = {1, 1}; // TODO - What does this number have to be to move the car at the speed that we want?
   public static final double[] SCALING_EJECTOR = {1};  // TODO - What does this number have to be to move the ejector at the speed that we want?
   
   public static final int MOTOR_DIRECTION_FORWARD = 1;
   public static final int MOTOR_DIRECTION_BACKWARD = -1;
   
   // Lift
   public static enum STATE_ADD_CHUTE_TOTE_TO_STACK
   {
      INIT,
      INTAKE_TOTE,
	   LIFT_TOTE,
	   HANDLE_CONTAINER,
	   JAWS_FINISH,
	   RESET
   }
   
   public static enum STATE_ADD_FLOOR_TOTE_TO_STACK
   {
      INIT,
      INTAKE_TOTE,
      LIFT_TOTE,
      HANDLE_CONTAINER,
      RESET
   }
   
   public static enum STATE_ADD_CONTAINER_TO_STACK
   {
      INIT,
      CONTAINER_CAR_EXTEND,
      LIFT_CAR,
      CONTAINER_FIXED_EXTEND,
      CONTAINER_CAR_CONTRACT,
      LOWER_CAR,
      RESET
   }
   
   public static enum STATE_EJECT_STACK
   {
      JAWS_EXTEND,
      LIFT_CAR,
      STACK_HOLDER_CONTRACT,
      LOWER_CAR,
      EJECT_STACK,
      RESET,
   }
   
   // Car
   public static enum POSITION_CAR
   {
	   STACK,
	   DESTACK,
	   CHUTE,
	   BOTTOM
   }
   
   // Ejector
   public static enum STATE_EJECT
   {
      PUSHER_EXTEND,
      MOVE_OUT,
      RESET
   }
   
   public static enum POSITION_EJECTOR
   {
      OUT,
      MID,  // TODO: If we do not use this, remove this enum and create boolean in ejector class
      IN
   }

   // Test mode
   public static final int TEST_MODE_PNEUMATIC = 0;
   public static final int TEST_MODE_CAR = 1;
   public static final int TEST_MODE_SENSOR = 2;
}
 