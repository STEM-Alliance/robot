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

    // PHYSICAL ------------------------------------------------------------------------------------
    public static final double DRIVE_MAX_RPM = 5300; // CIM: ~5300 rpm, MiniCIM: ~5800 rpm

    // CONFIG --------------------------------------------------------------------------------------
    public static final boolean[] ANGLE_MOTOR_INVERT = {true, true, true, true};

    // CALIBRATION ---------------------------------------------------------------------------------
    /** Value to use for converting raw input (-1 to 1) to speed (in rpm) that the sensor will detect */
    public static final double DRIVE_SPEED_MAX = DRIVE_MAX_RPM * DRIVE_EFFICIENCY * SHIFTER_SENSOR_RATIO;
    /** Leave wheel in current direction when stopping - Snapping to an angle would arc our path when moving again */
    public static final double DEADBAND_STOP_TURNING_AT_REST = 0.1;
}
