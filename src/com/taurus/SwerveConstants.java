/**
 * 
 */
package com.taurus;

/**
 * Pin assignments for Swerve Drive system
 * @author Team 4818 Taurus Robotics
 */
public class SwerveConstants {

    public static final int WheelCount = 4;
    
    /**
     * X & Y coordinate positions for wheel placement.
     * Relative to robot center
     */
    public static final double[][] WheelPositions = {   {-1, 1},    // front left
                                                        { 1, 1},    // front right
                                                        { 1,-1},    // back right
                                                        {-1,-1} };  // back left
    
    /**
     * Orientation Angle of each wheel in degrees clockwise.
     * Relative to robot 0 angle
     */
    public static final double[] WheelOrientationAngle = {  0,      // front left
                                                            90,     // front right
                                                            180,    // back right
                                                            270 };  // back left
    
    /**
     * Pins (Digital) for wheel speed encoder inputs
     */
    public static final int[][] WheelEncoderPins = {    { 1, 2},    // front left
                                                        { 3, 4},    // front right
                                                        { 5, 6},    // back right
                                                        { 7, 8} };  // back left
    
    /**
     * Pins (Analog) for wheel angle potentiometer inputs
     */
    public static final int[] WheelPotPins = {  2,
                                                3,
                                                4,
                                                5 };

    /**
     * Pins (PWM) for wheel drive motor controller outputs
     */
    public static final int[] WheelDriveMotorPins = {   1,
                                                        3,
                                                        5,
                                                        7 };
    
    /**
     * Pins (PWM) for wheel angle motor controller outputs
     */
    public static final int[] WheelAngleMotorPins = {   2,
                                                        4,
                                                        6,
                                                        8 };

    /**
     * Pin (PWM) for wheel shifting servos
     */
    public static final int WheelShiftServoPin = 9;
    
    public static final int GyroPin = 1;
    
    public static final int GearLow = 0;
    public static final int GearHigh = 1;
    
}
