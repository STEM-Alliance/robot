package com.taurus.robot;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
    // Robot Input and Output pins. Insert in order.
    
    public static final int CAN_ROCKER_TALONS_RIGHT[] = {10,12,14};
    public static final int CAN_ROCKER_TALONS_LEFT[] = {11,13,15};
    
    public static final int CAN_SHOOTER_TALON_TOP = 20;
    public static final int CAN_SHOOTER_TALON_BOTTOM = 21;
    public static final int CAN_SHOOTER_TALON_AIMER = 24;
    public static final int CAN_MANIPULATOR_TALON = 25;
    public static final int CAN_KICKER_TALON = 26;
    
    public static final int CAN_LIFT_TALON_L = 30;
    public static final int CAN_LIFT_TALON_R = 31;

    public static final int PIN_DIO_SHOOTER_BALL_SENSOR = 0;

    public static final int PIN_SERVO_SHOOTER_BALL_RELEASE_1 = 0;
    public static final int PIN_SERVO_SHOOTER_BALL_RELEASE_2 = 1;
    public static final int PIN_SERVO_CLAW_RELEASE = 4;
    
    public static final int PIN_SERVO_LIFT_BRAKE_L = 2;
    public static final int PIN_SERVO_LIFT_BRAKE_R = 3;
    
    public static final int PIN_RELAY_LEDS = 0;
    
    public static final int PIN_ANG_KICKER = 0;
}
