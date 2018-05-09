package org.wfrobotics.robot.config.robotConfigs;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public final class HerdBlackTank extends RobotConfig
{
    public HerdBlackTank()
    {
        //                      Intake
        // _________________________________________________________________________________
        INTAKE_SENSOR_R = 3;

        INTAKE_DISTANCE_TO_CUBE = 7.0; // centimeters
        INTAKE_TIMEOUT_JAWS = 0.5; // seconds
        INTAKE_TIMEOUT_WRIST = 0.5; //seconds
        INTAKE_INVERT_RIGHT = false;
        INTAKE_INVERT_LEFT = true;

        //                      Lift
        // _________________________________________________________________________________
        LIFT_MAX_POSSIBLE_UP = 0.0;
        LIFT_MAX_POSSIBLE_DOWN = 0.0;
        LIFT_MAX_POSSIBLE_VELOCITY = 0.0;
        LIFT_POSIBLE_VELOCITY_PERCENTAGE = 0.975;
        LIFT_P = new double[] {0.0, 0.0};
        LIFT_I = new double[] {0.0, 0.0};
        LIFT_D = new double[] {0.0, 0.0};
        LIFT_F = new double[] {1023.0 / LIFT_MAX_POSSIBLE_UP, 1023.0 / LIFT_MAX_POSSIBLE_DOWN};
        LIFT_VELOCITY = new int[] {(int) (LIFT_MAX_POSSIBLE_UP * LIFT_POSIBLE_VELOCITY_PERCENTAGE), (int) (LIFT_MAX_POSSIBLE_UP * LIFT_POSIBLE_VELOCITY_PERCENTAGE)};
        LIFT_ACCELERATION = new int[] {(int) (LIFT_VELOCITY[0] * 7.0), (int) (LIFT_VELOCITY[0] * 7.0)};

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
        TANK_DISTANCE_P = 0.31;
        TANK_DISTANCE_I = 0.005;
        TANK_DISTANCE_D =  0.68;
        TANK_DISTANCE_F = 1023.0 / TANK_MAX_VELOCITY;
        TANK_CRUISE_VELOCITY = (int) (TANK_MAX_VELOCITY * .9);
        TANK_ACCELERATION = new int[] {TANK_CRUISE_VELOCITY, TANK_CRUISE_VELOCITY};
        TANK_DISTANCE_IZONE = 20;

        TANK_PATH_P = 0.1625;
        TANK_PATH_I = 0.005;
        TANK_PATH_D = 0.65;
        TANK_PATH_IZONE = 35;

        TANK_GEAR_RATIO_ENCODER_TO_WHEEL_HIGH = 24.0 / 60.0;
        TANK_GEAR_RATIO_ENCODER_TO_WHEEL_LOW = 60.0 / 24.0;

        TANK_LEFT_INVERT = true;
        TANK_RIGHT_INVERT = false;
        TANK_LEFT_SENSOR_PHASE = false;
        TANK_RIGHT_SENSOR_PHASE = true;
        TANK_OPEN_LOOP_RAMP = 0.05;

        //                      Winch
        // _________________________________________________________________________________
        WINCH = 22;
        WINCH_INVERT = true;
        WINCH_SPEED = 1;
    }
}
