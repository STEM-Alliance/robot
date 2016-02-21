package com.taurus.robot;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // Robot Input and Output pins. Insert in order.

    public static int PIN_ROCKER_TALONS_RIGHT[] = {10,12,14};
    public static int PIN_ROCKER_TALONS_LEFT[] = {11,13,15};
    
    public static int PIN_SHOOTER_TALON_FT = 20;
    public static int PIN_SHOOTER_TALON_FB = 21;
    public static int PIN_SHOOTER_TALON_BT = 22;
    public static int PIN_SHOOTER_TALON_BB = 23;
    public static int PIN_SHOOTER_TALON_AIMER = 24;
    
    public static int PIN_LIFT_TALON_L = 30;
    public static int PIN_LIFT_TALON_R = 31;
    public static int PIN_LIFT_BRAKE_SERVO_L = 1;  // TODO - is this the correct pin?
    public static int PIN_LIFT_BRAKE_SERVO_R = 2;  // TODO - is this the correct pin?
    
    public static int PIN_SHOOTER_SENSOR_STOP = 0;  // TODO - DRL is this the correct pin?
    public static int PIN_SHOOTER_SERVO_BALLRELEASE = 0;  // TODO - DRL change to correct pin
}
