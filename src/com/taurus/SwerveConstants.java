/**
 * 
 */
package com.taurus;

/**
 * Pin assignments for Swerve Drive system
 * @author Team 4818 Taurus Robotics
 */
public class SwerveConstants {

    /*
     * Digital: 0-9 are on-board, 10-25 are on the MXP
     * Analog: 0-3 are on-board, 4-7 are on the MXP
     * PWM: 0-9 are on-board, 10-19 are on the MXP
     */
    
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
    public static final int[][] WheelEncoderPins = {    { 0, 1},    // front left
                                                        { 2, 3},    // front right
                                                        { 4, 5},    // back right
                                                        { 6, 7} };  // back left
    
    /**
     * Pins (Analog) for wheel angle potentiometer inputs
     */
    public static final int[] WheelPotPins = {  0,
                                                1,
                                                2,
                                                3 };

    /**
     * Pins (PWM) for wheel drive motor controller outputs
     */
    public static final int[] WheelDriveMotorPins = {   0,
                                                        2,
                                                        4,
                                                        6 };
    
    /**
     * Pins (PWM) for wheel angle motor controller outputs
     */
    public static final int[] WheelAngleMotorPins = {   1,
                                                        3,
                                                        5,
                                                        7 };

    /**
     * Pins (PWM) for wheel shifting servos
     */
    public static final int[] WheelShiftServoPins = { 8, 9 };
    
    /**
     * Pin (Analog) for the Gyroscope
     */
    public static final int GyroPin = 0;
    
    /**
     * Gear Low/High constants
     */
    public static final int GearLow = 0;
    public static final int GearHigh = 1;
    
    /**
     * Wheel diameter
     */
    public static final double DriveWheelDiameter = 4.0;  // inches
    
    /**
     * Wheel circumference
     */
    public static final double DriveWheelCircumference = Math.PI * DriveWheelDiameter;
    
    /**
     * Encoder pulses per 1 full encoder rotation
     */
    public static final int DriveEncoderPulses = 64;
    
    /**
     * Full encoder rotations per wheel rotation
     */
    public static final double DriveEncoderRotPerWheelRot = 3.0;
    
    /**
     * Inches per encoder pulse
     */
    public static final double DriveEncoderRate = DriveWheelCircumference / DriveEncoderRotPerWheelRot / DriveEncoderPulses;

    /**
     * The ratio between low gear top speed and high gear top speed
     */
    public static final double DriveGearRatio = 0.5;  // TODO: probably totally wrong
    
    /**
     * The wheel top speed in high gear
     */
    public static final double DriveHighGearMaxVelocity = DriveWheelCircumference * 9.5;

    /**
     * The wheel top speed in low gear
     */
    public static final double DriveLowGearMaxVelocity = DriveHighGearMaxVelocity * DriveGearRatio;
}
