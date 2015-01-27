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
   
   // Motors
   public static final int MOTOR_TALON_PIN_CAR = 999; //TODO - Which PWM is this talon connected to?;
   public static final double MOTOR_SPEED_CAR = 1; // TODO - What does this number have to be to move the car at the speed that we want?
   public static final int MOTOR_DIRECTION_UP = 1;
   public static final int MOTOR_DIRECTION_DOWN = -1;
   
   // Car
   public static enum POSITION_CAR
   {
	   STACK,
	   DESTACK,
	   CHUTE,
	   BOTTOM
   }
   
   // Test mode
   public static final int TEST_MODE_PNEUMATICS = 0;
   public static final int TEST_MODE_CAR = 1;
}
