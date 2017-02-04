package org.wfrobotics.commands;

/**
 * Constant values to be shared by commands. 
 * Constants should be reusable values, meaning they are useful across multiple commands.
 * @author drlindne
 *
 */
public class Constants 
{    
   public static double SHOOTER_READY_SHOOT_SPEED = 3750;  // Ideal speed for shooter to shoot balls
   public static double SHOOTER_READY_SHOOT_SPEED_TOLERANCE = .1;  // Ideal tolerance of RPMs for safely shooting the balls
   public static double AUGER_SPEED = 100;  // Ideal for giving balls to shooter while shooter maintains speed (above min shooter recovery time)
}
