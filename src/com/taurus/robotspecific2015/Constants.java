package com.taurus.robotspecific2015;

public abstract class Constants 
{
   // Pneumatics
   public static final int MODULE_ID_PCU = 9;
   public static final int MODULE_ID_PCU_2 = 10;
   
   public static final int[] CHANNEL_RAIL = {0, 1};
   public static final int[] CHANNEL_CONTAINER_CAR = {2, 3};
   public static final int[] CHANNEL_CONTAINER_FIXED = {4, 5};
   public static final int[] CHANNEL_STACK_HOLDER = {6, 7};
   public static final int[] CHANNEL_STOP = {0, 1};
   public static final int[] CHANNEL_PUSHER = {2, 3};   
   
   public static final double TIME_EXTEND_RAILS = .5; //TODO - what is the amount of time required to close the rails
   public static final double TIME_EXTEND_CONTAINER_CAR = .5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_EXTEND_CONTAINER_FIXED = .5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_EXTEND_STACK_HOLDER = .5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_EXTEND_STOP = .5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_EXTEND_PUSHER = .5; //TODO - what is the amount of time required to close the jaws
   
   public static final double TIME_CONTRACT_RAILS = .5; //TODO - what is the amount of time required to close the rails
   public static final double TIME_CONTRACT_CONTAINER_CAR = .5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_CONTRACT_CONTAINER_FIXED = .5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_CONTRACT_STACK_HOLDER = .5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_CONTRACT_STOP = .5; //TODO - what is the amount of time required to open the container car
   public static final double TIME_CONTRACT_PUSHER = .5; //TODO - what is the amount of time required to close the jaws
   
   // Motors
   public static final int[] PINS_LIFT_MOTOR = {8}; 
   public static final int[] PINS_EJECTOR = {9};
   
   public static final double[] SCALING_LIFT_MOTOR = {1}; 
   public static final double[] SCALING_EJECTOR = {1};  
   
   public static final int MOTOR_DIRECTION_FORWARD = 1;
   public static final int MOTOR_DIRECTION_BACKWARD = -1;
   
   //Sensors
   public static final int CHANNEL_DIGITAL_TOTE_INTAKE = 8;
   public static final int CHANNEL_DIGITAL_CAR_ZERO = 9;
   public static final int CHANNEL_DIGITAL_EJECTOR_OUT = 10;
   public static final int CHANNEL_DIGITAL_EJECTOR_IN = 11;
   public static final int[] LIFT_ENCODER_PINS = {0, 1};
   public static final double INCHES_PER_PULSE = .1;
   public static final double[] LIFT_POSTITIONS = {0, 1, 2, 3};
   
   // Lift
   public static enum STATE_ADD_CHUTE_TOTE_TO_STACK
   {
      INIT,
      INTAKE_TOTE,
	   LIFT_TOTE,
	   HANDLE_CONTAINER,
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
	   BOTTOM,
	   MOVING
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
   public static final int TEST_MODE_MOTORS = 1;
   public static final int TEST_MODE_SENSOR = 2;
   
   // Autonomous mode
   public static enum AUTO_STATE_MACHINE
   {
      AUTO_START, DRIVE_FOR, DRIVE_STOP, DRIVE_RIGHT, AUTO_END,
   }
}
 