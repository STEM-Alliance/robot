package org.wfrobotics.robot.config;

public class RobotMap
{
    /*
     * Digital: 0-9 are on-board, 10-25 are on the MXP
     * Analog: 0-3 are on-board, 4-7 are on the MXP
     * PWM: 0-9 are on-board, 10-19 are on the MXP
     */

    public static final int CAN_SWERVE_DRIVE_TALONS[] = { 1, 4, 5, 8};
    public static final int CAN_SWERVE_ANGLE_TALONS[] = { 2, 3, 6, 7};
    public static final int PWM_SWERVE_SHIFT_SERVOS[] = { 0, 1, 2, 3 };

    // Drive --------------------------------------------------------------------------------------
    public static final int CAN_TANK_DRIVE_TALONS_L[] = { 12, 13 };
    public static final int CAN_TANK_DRIVE_TALONS_R[] = { 10, 11 };
    public static final int CAN_TANK_SHIFTER_DEVICE = 7;
    public static final int CAN_TANK_SHIFTER_HIGH = 0;
    public static final int CAN_TANK_SHIFTER_LOW = 1;
    public static final boolean TANK_LEFT_INVERT = true;
    public static final boolean TANK_RIGHT_INVERT = false;

    // Intake Solenoid ----------------------------------------------------------------------------
    public static final int CAN_PNEUMATIC_CONTROL_MODULE = 7;
    public static final int PNEUMATIC_INTAKE_FORWARD = 3;
    public static final int PNEUMATIC_INTAKE_REVERSE = 4;

    // Intake Wheels ------------------------------------------------------------------------------
    public static final int CAN_INTAKE_LEFT = 19;
    public static final int CAN_INTAKE_RIGHT = 20;
    public static final int DIGITAL_INTAKE_DISTANCE = 2;

    // LED ----------------------------------------------------------------------------------------
    public static final int CAN_LIGHT = 31;

    // Lift ---------------------------------------------------------------------------------------
    public static final int CAN_LIFT = 18;
    public static final int DIGITAL_LIFT_LIMIT_BOTTOM = 0;
    public static final int DIGITAL_LIFT_LIMIT_TOP = 1;
}
