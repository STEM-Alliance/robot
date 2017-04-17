package org.wfrobotics.robot.config;

/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
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
    //public static final int DIO_SWERVE_CAL[] = {1,2,3,4};
    public static final int PWM_SERVO_GEAR_INTAKE = 4;

    public static final int NEW_FEEDER_MOTOR_SRX = 21;
    public static final int FEEDER_MOTOR_SRX = 25; //Change if necessary and config 

    public static final int INTAKE_MOTOR_SRX = 23;
    
    public static final int CLIMBER_MOTOR_SRX = 24;
    //public static final int CLIMBER_SENSOR_DIGITAL = 0;
   
    public static final int CAN_LIGHT = 31;
    
    public static final int AUGER_MOTOR = 10; //Change if necessary and config
    
    public static final int LIFTER_MOTOR = 22;  // Previously 23 was the left intake
    public static final int LIFTER_SENSOR_GEAR = 0;
}
