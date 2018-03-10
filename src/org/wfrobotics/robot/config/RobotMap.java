package org.wfrobotics.robot.config;

public class RobotMap
{
    /*
     * Digital: 0-9 are on-board, 10-25 are on the MXP
     * Analog: 0-3 are on-board, 4-7 are on the MXP
     * PWM: 0-9 are on-board, 10-19 are on the MXP
     */

    // Unused -------------------------------------------------------------------------------------
    public static final int CAN_SWERVE_DRIVE_TALONS[] = { 1, 4, 5, 8};
    public static final int CAN_SWERVE_ANGLE_TALONS[] = { 2, 3, 6, 7};
    public static final int PWM_SWERVE_SHIFT_SERVOS[] = { 0, 1, 2, 3 };

    // Drive --------------------------------------------------------------------------------------
    public static final int CAN_TANK_DRIVE_TALONS_L[] = { 15, 17 };
    public static final int CAN_TANK_DRIVE_TALONS_R[] = { 16, 14 };
    public static final int CAN_TANK_SHIFTER_DEVICE = 7;
    public static final int CAN_TANK_SHIFTER_HIGH = 0;
    public static final int CAN_TANK_SHIFTER_LOW = 1;
    public static final double TANK_GEAR_RATIO_ENCODER_TO_WHEEL_HIGH = 24.0 / 60.0;  // Adjacent to encoder
    public static final double TANK_GEAR_RATIO_ENCODER_TO_WHEEL_LOW = 60.0 / 24.0;  // Adjacent to encoder
    public static final double TANK_GEAR_RATIO_MOTOR_TO_ENCODER = 36.0 / 15.0;  // Wheel to adjacent
    public static final double TANK_WHEEL_CIRCUMFERENCE = (6.25 * .995) * Math.PI;  // 6.25 adjusted for thread wear
    public static final double DISTANCE_SEPERATION_WHEEL_SIDES = 25;


    // Field --------------------------------------------------------------------------------------
    public static final double kScaleClearWhenDownInches = 53.5;
    public static final double kSwitchClearInches = 13;

    // Intake -------------------------------------------------------------------------------------
    public static final int CAN_PNEUMATIC_CONTROL_MODULE = 7;

    // Intake Lifting -----------------------------------------------------------------------------
    public static final int CAN_INTAKE_LIFT = 21;
    public static final int INTAKE_LIFT_FORWARD_LIMIT = 4600;
    public static final int INTAKE_LIFT_REVERSE_LIMIT= 0;

    // Intake Wheels ------------------------------------------------------------------------------
    public static final int CAN_INTAKE_LEFT = 19;
    public static final int CAN_INTAKE_RIGHT = 20;

    // Jaws ---------------------------------------------------------------------------------------
    public static final int PNEUMATIC_INTAKE_HORIZONTAL_FORWARD = 2;
    public static final int PNEUMATIC_INTAKE_HORIZONTAL_REVERSE = 3;

    // LED ----------------------------------------------------------------------------------------
    public static final int CAN_LIGHT = 0;

    // Lift ---------------------------------------------------------------------------------------
    public static final int CAN_LIFT_R = 10;
    public static final int CAN_LIFT_L = 11;

    // Wrist --------------------------------------------------------------------------------------
    public static final int PNEUMATIC_INTAKE_VERTICAL_FORWARD = 4;
    public static final int PNEUMATIC_INTAKE_VERTICAL_REVERSE = 5;
}
