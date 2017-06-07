package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.vision.VisionPivot;
import org.wfrobotics.reuse.commands.vision.VisionStrafe;

/**
 * Constant values to be shared by commands
 * Constants should be reusable values, meaning they are useful across multiple commands
 */
public class Commands 
{
   public static final double AUTONOMOUS_TIME_DRIVE_MODE = 4;
   public static final double AUTONOMOUS_DRIVE_SPEED = .6;
   public static final double AUTONOMOUS_TURN_SPEED = .5;
   public static final double AUTONOMOUS_TURN_TOLERANCE = .1;
   
   public static final VisionPivot.Config GEAR_VISION_PIVOT_CONFIG = new VisionPivot.Config(1.8, 0, 10, .35, .125, true, true);
   public static final VisionStrafe.Config GEAR_VISION_STRAFE_CONFIG = new VisionStrafe.Config(1.85, .0001, 10, .6, .2, false, true); 
   
   public static final double SHOOTER_READY_SHOOT_SPEED = 4300;  // Ideal speed for shooter to shoot balls
   //public static final double SHOOTER_READY_SHOOT_SPEED_TOLERANCE = .1;  // Ideal tolerance of RPMs for safely shooting the balls
   public static final double SHOOTER_READY_SHOOT_SPEED_TOLERANCE_RPM = 1500;  // Ideal tolerance of RPMs for safely shooting the balls
   public static final int SHOOTER_READY_CONSECUTIVE_SAMPLES = 10; // How many times through the command to be "reved"
   public static final double SHOOTER_TRIGGER_SPEED_DROP = SHOOTER_READY_SHOOT_SPEED * 0.05 * 0.5 ; // Speed for the unjamming gate (
   
   public static final double AUGER_UNJAM_SPEED = -0.3;
   public static final double AUGER_SPEED = 0.45;  // Ideal for giving balls to shooter while shooter maintains speed (above min shooter recovery time)
   public static final double AUGER_UNJAM_PERIOD = .1; // Percent of AUGER_UNJAM_PERIOD to unjam (Ex: .1 -> 90% shoot, 10% unjam, repeat)
   public static final double AUGER_NORMAL_PERIOD = 1.4; // Seconds between each unjam attempt

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