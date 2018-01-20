package org.wfrobotics.robot.config;

public class RobotMap
{
    /*
     * Digital: 0-9 are on-board, 10-25 are on the MXP
     * Analog: 0-3 are on-board, 4-7 are on the MXP
     * PWM: 0-9 are on-board, 10-19 are on the MXP
     */

    public static final int ANG_SWERVE_ANGLE[] = { 0, 1, 2, 3 };
    public static final int CAN_SWERVE_DRIVE_TALONS[] = { 1, 4, 5, 8};
    public static final int CAN_SWERVE_ANGLE_TALONS[] = { 2, 3, 6, 7};
    public static final int PWM_SWERVE_SHIFT_SERVOS[] = { 0, 1, 2, 3 };


    public static final int CAN_TANK_DRIVE_TALONS_L[] = { 10, 11 };
    public static final int CAN_TANK_DRIVE_TALONS_R[] = { 12, 13 };
    
    public static final int CAN_LIGHT = 31;

}
