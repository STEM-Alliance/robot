package org.wfrobotics.robot.config.robotConfigs;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public class HerdBlackTank extends RobotConfig
{
    public HerdBlackTank()
    {
        //                      Intake
        // _________________________________________________________________________________
        INTAKE_SENSOR_R = 3;
        INTAKE_SENSOR_L = 2;

        INTAKE_DISTANCE_TO_CUBE = 7.0; // centimeters
        INTAKE_TIMEOUT_JAWS = 0.5; // seconds
        INTAKE_TIMEOUT_WRIST = 0.5; //seconds
        INTAKE_INVERT_RIGHT = false;
        INTAKE_INVERT_LEFT = true;

        //                      Lift
        // _________________________________________________________________________________
        LIFT_MAX_POSSIBLE_UP = 1025;  // DRL 2-19-18
        LIFT_POSIBLE_VELOCITY_PERCENTAGE = 0.975;
        LIFT_P = .1 * 1023.0 / 1000.0 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 2 * 1.0;
        LIFT_I = LIFT_P * .01 * .1;
        LIFT_D = LIFT_P * 10.0 * .40;
        LIFT_F = 1023.0 / LIFT_MAX_POSSIBLE_UP;
        LIFT_VELOCITY = (int) (LIFT_MAX_POSSIBLE_UP * LIFT_POSIBLE_VELOCITY_PERCENTAGE);
        LIFT_ACCELERATION = (int) (LIFT_VELOCITY * 7.0);

        LIFT_MOTOR_INVERTED_LEFT = false; // left
        LIFT_MOTOR_INVERTED_RIGHT = true; // right

        LIFT_SENSOR_PHASE_LEFT = true; // left
        LIFT_SENSOR_PHASE_RIGHT = true; // right

        LIFT_LIMIT_SWITCH_NORMALLY = new LimitSwitchNormal[][] {
            { LimitSwitchNormal.NormallyOpen, LimitSwitchNormal.NormallyOpen},  // Left Fwd
            { LimitSwitchNormal.NormallyOpen, LimitSwitchNormal.NormallyOpen}  // Right Fwd
        };

        //                      Tank
        // _________________________________________________________________________________
        TANK_MAX_VELOCITY = (10000 + 10300) / 2.0;  // 2-28-18
        TANK_P = 0.31;
        TANK_I = 0.005;
        TANK_D =  0.68;
        TANK_F = 1023.0 / TANK_MAX_VELOCITY;
        TANK_CRUISE_VELOCITY = (int) (TANK_MAX_VELOCITY * .9);
        TANK_ACCELERATION = new int[] {TANK_CRUISE_VELOCITY, TANK_CRUISE_VELOCITY};
        TANK_IZONE = 20;

        TANK_GEAR_RATIO_ENCODER_TO_WHEEL_HIGH = 24.0 / 60.0;
        TANK_GEAR_RATIO_ENCODER_TO_WHEEL_LOW = 60.0 / 24.0;

        TANK_LEFT_INVERT = true;
        TANK_RIGHT_INVERT = false;

        TANK_LEFT_SENSOR_PHASE = false;
        TANK_RIGHT_SENSOR_PHASE = true;

        TANK_OPEN_LOOP_RAMP = 0.5;

        //                      Winch
        // _________________________________________________________________________________
        WINCH = 22;
        WINCH_INVERT = true;
        WINCH_SPEED = 1;
    }
}
