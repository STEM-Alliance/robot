package org.wfrobotics.robot.config.robotConfigs;

public class HerdVictor extends RobotConfig
{
    public HerdVictor()
    {
        INTAKE_DISTANCE_TO_CUBE = 7; // centimeters
        INTAKE_WRIST_TIMEOUT_LENTH = 0.5; //secounds

        LIFT_MAX_POSSIBLE_UP = 2250;
        LIFT_P = .1 * 1023.0 / 1000.0 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2;
        LIFT_I = LIFT_P * .001 * 0;
        LIFT_D = LIFT_P * 10.0 * 0;
        LIFT_F = 1023.0 / LIFT_MAX_POSSIBLE_UP;
        LIFT_POSIBLE_VELOCITY_PERCENTAGE = 0.8;

        LIFT_MOTOR_INVERTED_LEFT = false; // left
        LIFT_MOTOR_INVERTED_RIGHT = true; // right

        LIFT_SENSOR_PHASE_LEFT = true; // left
        LIFT_SENSOR_PHASE_RIGHT = true; // right

        TANK_MAX_VELOCITY = 10000.0;
        TANK_P = 1.25;
        TANK_I = TANK_P * 0.005;
        TANK_D =  TANK_P * 2.5;
        TANK_F = 1023 /  TANK_MAX_VELOCITY;

        TANK_CRUISE_VELOCITY = 7575;
        TANK_ACCELERATION = 7575;

        TANK_LEFT_INVERT = true;
        TANK_RIGHT_INVERT = false;

        TANK_LEFT_SENSOR_PHASE = false;
        TANK_RIGHT_SENSOR_PHASE = false;
    }
}
