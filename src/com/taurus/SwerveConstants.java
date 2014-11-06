/**
 * 
 */
package com.taurus;

/**
 * @author 
 * Pin assignments for Swerve Drive system
 */
public class SwerveConstants {
    
    /**
     * X & Y coordinate positions for wheel placement.
     * Relative to robot center
     */
    public static final double[][] WheelPositions = {   {-1, 1},    // front left
                                                        { 1, 1},    // front right
                                                        { 1,-1},    // back right
                                                        {-1,-1} };  // back left
    
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
    public static final int[] WheelPotPins = {          1,
                                                        2,
                                                        3,
                                                        4 };

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
     * Pins (PWM) for wheel shifting servo
     */
    public static final int[] WheelShiftServoPins = {   9,
                                                        10,
                                                        11,
                                                        12 };
    
    public static final int GyroPin = 5;
    
    public static final int GearLow = 0;
    public static final int GearHigh = 1;
    
}
