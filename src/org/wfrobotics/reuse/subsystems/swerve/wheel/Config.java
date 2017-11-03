package org.wfrobotics.reuse.subsystems.swerve.wheel;

/**
 * @author Team 4818 WFRobotics
 */
public class Config
{
    private static final double DRIVE_EFFICIENCY = .8;  // Estimate of efficiency of the gearing from motor to sensor TODO
    private static final double SHIFTER_SENSOR_RATIO = 14.0 / 10.0;  // The gear ratio conversion from motor output to sensor

    // PID ----------------------------------------------------------------------------------------
    public static final double DRIVE_P = .007;
    public static final double DRIVE_I = .000;
    public static final double DRIVE_D = 0.00;
    public static final double DRIVE_F = .04;

    public static final double ANGLE_P = .018;
    public static final double ANGLE_I = 0.0000075;
    public static final double ANGLE_D = 0.0001;

    // PHYSICAL ------------------------------------------------------------------------------------
    public static final double DRIVE_MAX_RPM = 5300; // CIM: ~5300 rpm, MiniCIM: ~5800 rpm

    // CONFIG --------------------------------------------------------------------------------------
    public static final boolean[] ANGLE_MOTOR_INVERT = {true, true, true, true};

    // CALIBRATION ---------------------------------------------------------------------------------
    /** Max speed the rotation can spin, relative to motor maximum */
    public static double ANGLE_MAX_SPEED = -.7;
    /** Value to use for converting raw input (-1 to 1) to speed (in rpm) that the sensor will detect */
    public static final double DRIVE_SPEED_MAX = DRIVE_MAX_RPM * DRIVE_EFFICIENCY * SHIFTER_SENSOR_RATIO;
    public static final double DEADBAND_MINIMUM_SPEED = 0.1;

    public static final double DRIVE_RAMP_LOW = 5;
    public static final double DRIVE_RAMP_HIGH = 30;
}
