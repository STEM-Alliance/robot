package org.wfrobotics.commands;

/**
 * Constant values to be shared by commands. 
 * Constants should be reusable values, meaning they are useful across multiple commands.
 *
 */
public class Constants 
{
   public static final double AUTONOMOUS_DRIVE_SPEED = .5;
   public static final double AUTONOMOUS_TURN_SPEED = .5;
   public static final double AUTONOMOUS_TURN_TOLERANCE = .1;
   
   public static final double SHOOTER_READY_SHOOT_SPEED = 3750;  // Ideal speed for shooter to shoot balls
   //public static final double SHOOTER_READY_SHOOT_SPEED_TOLERANCE = .1;  // Ideal tolerance of RPMs for safely shooting the balls
   public static final double SHOOTER_READY_SHOOT_SPEED_TOLERANCE_RPM = 100;  // Ideal tolerance of RPMs for safely shooting the balls
   public static final double SHOOTER_TRIGGER_SPEED_DROP = SHOOTER_READY_SHOOT_SPEED * 0.05 * 0.5 ; // Speed for the unjamming gate (
   
   
   public static final double AUGER_UNJAM_SPEED = -0.4;
   public static final double AUGER_SPEED = 0.3;  // Ideal for giving balls to shooter while shooter maintains speed (above min shooter recovery time)
   public static final double AUGER_UNJAMING_TIME = 0.25; //secounds
   public static final double AUGER_TIME_SINCE_BALL = 5; //secounds

   public static final int INTAKE_OFF_ANGLE = 25;
   public static final int INTAKE_OFF_TIMEOUT = 1;
   
   public static final double CLIMBER_CLIMB_SPEED = 1; // 1 == full up; -1 == full down
   public static final double CLIMBER_CLIMB_TIME_AFTER_TOP_REACHED = .5; // TODO THIS NEEDS TO BE TESTED!! 
   
   public static final double TARGET_HEIGHT_IN = 8; //top of top tape to bottom of bottom tape (8 inches)
   public static final double TEST_TARGET_HEIGHT_PIXEL = 0; //number of pixels from top of top tap to bottom of bottom tape at ideal distance
   public static final double TEST_TARGET_DISTANCE_IN = 0; //optimal distance from boiler (needs to be tested)
   public static final double FOCAL_LENGTH_IN = TEST_TARGET_HEIGHT_PIXEL * TEST_TARGET_DISTANCE_IN / TARGET_HEIGHT_IN;
   public static final double OPTIMAL_SHOOTING_DISTANCE = 0;
   public static final double OPTIMAL_GEAR_DROP_OFF_DISTANCE = 0;

}
