/**
 * 
 */
package com.taurus.swerve;

/**
 * Pin assignments for Swerve Drive system
 * 
 * @author Team 4818 Taurus Robotics
 */
public class SwerveConstants {

    /*
     * Digital: 0-9 are on-board, 10-25 are on the MXP Analog: 0-3 are on-board,
     * 4-7 are on the MXP PWM: 0-9 are on-board, 10-19 are on the MXP
     */

    public static final int WheelCount = 4;

    public static final double ChassisWidth = 37.5;
    public static final double ChassisDepth = 23;
    public static final double ChassisScale = ChassisWidth;

    /**
     * X & Y coordinate positions for wheel placement. Relative to robot center
     */
    public static final double[][] WheelPositions = {
            { -ChassisWidth / ChassisScale, ChassisDepth / ChassisScale }, // front
                                                                           // left
            { ChassisWidth / ChassisScale, ChassisDepth / ChassisScale }, // front
                                                                          // right
            { ChassisWidth / ChassisScale, -ChassisDepth / ChassisScale }, // back
                                                                           // right
            { -ChassisWidth / ChassisScale, -ChassisDepth / ChassisScale } }; // back
                                                                              // left

    /**
     * Orientation Angle of each wheel in degrees clockwise. Relative to robot 0
     * angle
     */
    public static final double[] WheelOrientationAngle = { 115, // front left
            301, // front right
            2109, // back right
            293 }; // back left

    /**
     * Pins (Digital) for wheel speed encoder inputs
     */
    public static final int[][] WheelEncoderPins = { { 0, 1 }, // front left
            { 2, 3 }, // front right
            { 4, 5 }, // back right
            { 6, 7 } }; // back left

    /**
     * Pins (Analog) for wheel angle potentiometer inputs
     */
    public static final int[] WheelPotPins = { 0, 1, 2, 3 };

    /**
     * CAN Address for wheel drive motor controller outputs
     */
    public static final int[] WheelDriveMotorAddress = { 11, 12, 13, 14 };

    /**
     * CAN Address for wheel angle motor controller outputs
     */
    public static final int[] WheelAngleMotorPins = { 15, 16, 17, 18 };

    /**
     * Pins (PWM) for wheel shifting servos
     */
    public static final int[] WheelShiftServoPins = { 4, 5, 6, 7 };

    /**
     * Array of values for shifting gears, high then low values
     */
    public static final int[] WheelShiftServoVals[] = { { 45, 120 },
            { 120, 45 }, { 45, 120 }, { 120, 45 } };

    /**
     * Wheel diameter
     */
    public static final double DriveWheelDiameter = 4.0; // inches

    /**
     * Wheel circumference
     */
    public static final double DriveWheelCircumference = Math.PI
            * DriveWheelDiameter;

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
    public static final double DriveEncoderRate = DriveWheelCircumference
            / DriveEncoderRotPerWheelRot / DriveEncoderPulses;

    /**
     * The ratio between low gear top speed and high gear top speed
     */
    public static final double DriveGearRatio = 0.5; // TODO: probably totally
                                                     // wrong

    /**
     * The wheel top speed in high gear
     */
    public static final double DriveHighGearMaxVelocity = DriveWheelCircumference * 9.5;

    /**
     * The wheel top speed in low gear
     */
    public static final double DriveLowGearMaxVelocity = DriveHighGearMaxVelocity
            * DriveGearRatio;

    public static final double DriveSpeedCrawl = .4;
    public static final double DriveSpeedNormal = .85;
    
    
    /**
     * Digital input pins for the angle calibration reed switch
     */
    public static final int[] WheelAngleCalibrationPins = { 1, 2, 3, 8 };
}
