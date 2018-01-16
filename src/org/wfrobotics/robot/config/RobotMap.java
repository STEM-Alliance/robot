package org.wfrobotics.robot.config;

public class RobotMap
{
    /*
     * Digital: 0-9 are on-board, 10-25 are on the MXP
     * Analog: 0-3 are on-board, 4-7 are on the MXP
     * PWM: 0-9 are on-board, 10-19 are on the MXP
     */

    public static final int CAN_TANK_DRIVE_TALONS[] = { 5, 6, 7, 8};
    
    public static final int LIFTER_MOTOR = 1;
    
    public static final int INTAKE_MOTOR[] = { 2, 3 };
    public static final int CLIMB_MOTOR    =    4;  
    
    public static final int CAN_LIGHT = 31;

}
