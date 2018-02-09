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


    public static final int CAN_TANK_DRIVE_TALONS_L[] = { 13, 12 };
    public static final int CAN_TANK_DRIVE_TALONS_R[] = { 11, 10 };
    public static final int CAN_TANK_SHIFTER_DEVICE = 7;
    public static final int CAN_TANK_SHIFTER_HIGH = 0;
    public static final int CAN_TANK_SHIFTER_LOW = 1;
    public static final boolean TANK_LEFT_INVERT = true;
    public static final boolean TANK_RIGHT_INVERT = false;

    public static final int CAN_LIGHT = 31;
    /** gear ratio between encoder & wheel on black bot in high gear*/
    public static final double highRatio = 3.0;//11.0/3;
    /** gear ratio between encoder & wheel on black bot in low gear*/
    public static final double lowRatio = 25.0/3.0;
    /** gear ratio between drive shaft & wheels on black bot*/
    public static final double driveRatio = 2.0;//36.0/15;

    public static final double wheelCircumference = 6.21234794536479395120962303071*Math.PI;//20;
    public static final double robotWidth = 13.0;
}
