/**
 * 
 */
package org.wfrobotics.subsystems.drive.swerve;

/**
 * Pin assignments for Swerve Drive system
 * 
 * @author Team 4818 WFRobotics
 */
public class SwerveConstants {

    public static final int WheelCount = 4;

    public static final double ChassisWidth = 24.75;
    public static final double ChassisDepth = 28.5;
    public static final double ChassisScale = ChassisDepth;

    /**
     * X & Y coordinate positions for wheel placement. Relative to robot center
     */
    public static final double[][] WheelPositions = {
            { -ChassisWidth / ChassisScale, ChassisDepth / ChassisScale }, // front left
            { ChassisWidth / ChassisScale, ChassisDepth / ChassisScale }, // front right
            { ChassisWidth / ChassisScale, -ChassisDepth / ChassisScale }, // back right
            { -ChassisWidth / ChassisScale, -ChassisDepth / ChassisScale } }; // back left

    /**
     * Orientation Angle of each wheel in degrees clockwise. Relative to robot 0
     * angle
     */
    public static final double[] WheelOrientationAngle = { 115, // front left
            301, // front right
            210, // back right
            293 }; // back left

    /**
     * Digital input pins for the angle calibration reed switch
     */
    public static final int[] WheelAngleCalibrationPins = { 1, 2, 3, 8 };


    /**
     * angle PID values
     */
    public static final double ANGLE_PID_P = .025;
    public static final double ANGLE_PID_I = 0.0001;
    public static final double ANGLE_PID_D = 0.00;
    
    /**
     * Array of values for shifting gears, low then high values
     */
    public static final int[] WheelShiftServoVals[] = { { 120, 45 }, { 45, 120 },
         { 120, 45 }, { 45, 120 } };

    public static final boolean WheelShiftDefaultHigh = false;

    /**
     * Minimum speed to cutoff motor output to prevent stalling
     */
    public static final double DRIVE_MOTOR_SPEED_MIN = .05;

    /**
     * if the speed sensor is in use
     */
    public static final boolean DRIVE_MOTOR_SPEED_SENSOR_ENABLE = false;

    /**
     * drive PID values
     */
    public static final double DRIVE_PID_P = .01;
    public static final double DRIVE_PID_I = .00;
    public static final double DRIVE_PID_D = .00;
    public static final double DRIVE_PID_F = .00;
    
    /**
     * voltage ramp ranges for calculations
     */
    public static final double DRIVE_RAMP_LOW = 5;
    public static final double DRIVE_RAMP_HIGH = 30;
    
    /**
     * Max RPM output of the motor
     * ~5300 for CIM
     * ~5800 for Mini CIM
     */
    public static final double DriveMaxRPM = 5300;
    
    /**
     * Estimate of efficiency of the gearing from motor to sensor
     */
    public static final double DriveEfficiency = .8;
    
    /**
     * The gear ratio conversion from motor output to sensor
     */
    public static final double DriveGearRatio = 12 / 48 * 12;
    
    /**
     * Value to use for converting raw input (-1 to 1) to speed (in rpm)
     * that the sensor will detect
     */
    public static final double DriveMaxSpeed = DriveMaxRPM * DriveEfficiency * DriveGearRatio;
    
    public static final double DriveSpeedCrawl = .4;
    public static final double DriveSpeedNormal = .85;
}
