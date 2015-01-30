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
   public static final double TIME_CLOSE_JAWS = 5; //TODO - what is the amount of time required to close the jaw
   
   // Motors
   public static final int MOTOR_TALON_PIN_CAR = 999; //TODO - Which PWM is this talon connected to?;
   public static final double MOTOR_SPEED_CAR = 1; // TODO - What does this number have to be to move the car at the speed that we want?
   public static final int MOTOR_DIRECTION_UP = 1;
   public static final int MOTOR_DIRECTION_DOWN = -1;
   
   // Lift
   public static enum STATE_ADD_TOTE_TO_STACK
   {
      INTAKE_TOTE,
	   LIFT_TOTE,
	   JAWS_CONTRACT,
	   JAWS_FINISH,
	   LOWER_CAR
   }
   
   // Car
   public static enum POSITION_CAR
   {
	   STACK,
	   DESTACK,
	   CHUTE,
	   BOTTOM
   }

   // Test mode
   public static final int TEST_MODE_PNEUMATIC = 0;
   public static final int TEST_MODE_CAR = 1;
   public static final int TEST_MODE_SENSOR = 2;
}
 