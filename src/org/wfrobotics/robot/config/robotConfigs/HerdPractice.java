package org.wfrobotics.robot.config.robotConfigs;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public class HerdPractice extends RobotConfig
{
    public HerdPractice()
    {
        //                      Intake
        // _________________________________________________________________________________
        INTAKE_SENSOR_R = 0;
        INTAKE_SENSOR_L = 1;

        INTAKE_DISTANCE_TO_CUBE = 6.6; // centimeters
        INTAKE_TIMEOUT_JAWS = 0.5; // seconds
        INTAKE_TIMEOUT_WRIST = 0.5; //seconds
        INTAKE_INVERT_RIGHT = false;
        INTAKE_INVERT_LEFT = true;

        //                      Lift
        // _________________________________________________________________________________
        LIFT_MAX_POSSIBLE_UP = 3100;  // Down 5000
        LIFT_POSIBLE_VELOCITY_PERCENTAGE = 0.8;
        LIFT_P = .1 * 1023.0 / 1000.0 * 2 * 2 * 2 * 2 * 2 * 2 * 2.5;
        LIFT_I = LIFT_P * .01 * 0;
        LIFT_D = LIFT_P * 10.0 * .25;
        LIFT_F = 1023.0 / LIFT_MAX_POSSIBLE_UP;
        LIFT_VELOCITY = (int) (LIFT_MAX_POSSIBLE_UP * LIFT_POSIBLE_VELOCITY_PERCENTAGE);
        LIFT_ACCELERATION = LIFT_VELOCITY;

        LIFT_MOTOR_INVERTED_LEFT = false; // left
        LIFT_MOTOR_INVERTED_RIGHT = true; // right

        LIFT_SENSOR_PHASE_LEFT = true; // left
        LIFT_SENSOR_PHASE_RIGHT = true; // right

        LIFT_LIMIT_SWITCH_NORMALLY = new LimitSwitchNormal[][] {
            { LimitSwitchNormal.NormallyClosed, LimitSwitchNormal.NormallyClosed},  // Left Fwd
            { LimitSwitchNormal.NormallyClosed, LimitSwitchNormal.NormallyClosed}  // Right Fwd
        };

        //                      Tank
        // _________________________________________________________________________________
        TANK_MAX_VELOCITY = 10000.0;
        TANK_P = 1.25;
        TANK_I = TANK_P * 0.005;
        TANK_D =  TANK_P * 2.5;
        TANK_F = 1023 /  TANK_MAX_VELOCITY;
        TANK_CRUISE_VELOCITY = 7575;
        TANK_ACCELERATION = new int[] {TANK_CRUISE_VELOCITY, TANK_CRUISE_VELOCITY};
        TANK_IZONE = 0;

        TANK_LEFT_INVERT = true;
        TANK_RIGHT_INVERT = false;

        TANK_LEFT_SENSOR_PHASE = false;
        TANK_RIGHT_SENSOR_PHASE = false;

        TANK_OPEN_LOOP_RAMP = 0.5;

        //                      Winch
        // _________________________________________________________________________________
        WINCH = 22;
        WINCH_INVERT = true;
        WINCH_SPEED = 1;
    }
}
