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

    public static final int WHEEL_COUNT = 4;

    public static final double CHASSIS_WIDTH = 24.75;
    public static final double CHASSIS_DEPTH = 28.5;
    public static final double CHASSIS_SCALE = CHASSIS_DEPTH;

    /**
     * X & Y coordinate positions for wheel placement. Relative to robot center
     */
    public static final double[][] POSITIONS = {
            { -CHASSIS_WIDTH / CHASSIS_SCALE, CHASSIS_DEPTH / CHASSIS_SCALE }, // front left
            { CHASSIS_WIDTH / CHASSIS_SCALE, CHASSIS_DEPTH / CHASSIS_SCALE }, // front right
            { CHASSIS_WIDTH / CHASSIS_SCALE, -CHASSIS_DEPTH / CHASSIS_SCALE }, // back right
            { -CHASSIS_WIDTH / CHASSIS_SCALE, -CHASSIS_DEPTH / CHASSIS_SCALE } }; // back left

    /**
     * Orientation Angle of each wheel in degrees clockwise. Relative to robot 0
     * angle
     */
    public static final double[] ANGLE_OFFSET = { 115, // front left
                                                  301, // front right
                                                  210, // back right
                                                  293 }; // back left

    /**
     * angle PID values
     */
    public static final double ANGLE_PID_P = .030;
    public static final double ANGLE_PID_I = 0.0001;
    public static final double ANGLE_PID_D = 0.00;
    
    /**
     * Array of values for shifting gears, low then high values
     */
    public static final int[] SHIFTER_VALS[] = { { 145, 45 }, 
                                                 { 60, 145 },
                                                 { 145, 45 }, 
                                                 { 0, 120 } };

    public static final boolean SHIFTER_DEFAULT_HIGH = false;

    /**
     * Minimum speed to cutoff motor output to prevent stalling
     */
    public static final double DRIVE_SPEED_MIN = .05;

    /**
     * if the speed sensor is in use
     */
    public static final boolean DRIVE_SPEED_SENSOR_ENABLE = true;

    /**
     * drive PID values
     */
    public static final double DRIVE_PID_P = .007;
    public static final double DRIVE_PID_I = .000;
    public static final double DRIVE_PID_D = 0.00;
    public static final double DRIVE_PID_F = .04;
    
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
    public static final double DRIVE_MAX_RPM = 5300;
    
    /**
     * Estimate of efficiency of the gearing from motor to sensor
     * TODO
     */
    public static final double DRIVE_EFFICIENCY = .8;
    
    /**
     * The gear ratio conversion from motor output to sensor
     */
    public static final double DRIVE_GEAR_SENSOR_RATIO = 14.0 / 10.0;
    
    /**
     * Value to use for converting raw input (-1 to 1) to speed (in rpm)
     * that the sensor will detect
     */
    public static final double DRIVE_MAX_SPEED = DRIVE_MAX_RPM * DRIVE_EFFICIENCY * DRIVE_GEAR_SENSOR_RATIO;
    
    public static final double DRIVE_SPEED_CRAWL = .4;
}
