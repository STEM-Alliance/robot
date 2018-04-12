package org.wfrobotics.robot.config;

public class RobotMap
{
    /*
     * Digital: 0-9 are on-board, 10-25 are on the MXP
     * Analog: 0-3 are on-board, 4-7 are on the MXP
     * PWM: 0-9 are on-board, 10-19 are on the MXP
     */

    // Drive --------------------------------------------------------------------------------------
    public static final int CAN_TANK_DRIVE_TALONS_L[] = { 15, 17 };
    public static final int CAN_TANK_DRIVE_TALONS_R[] = { 16, 14 };

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
