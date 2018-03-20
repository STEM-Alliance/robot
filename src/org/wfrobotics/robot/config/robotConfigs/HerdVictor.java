package org.wfrobotics.robot.config.robotConfigs;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public class HerdVictor extends RobotConfig
{
    public HerdVictor()
    {
        //                      Intake
        // _________________________________________________________________________________
        INTAKE_SENSOR_R = 3;
        INTAKE_SENSOR_L = 2;

        INTAKE_DISTANCE_TO_BUMPER = 17.22;  // centimeters
        INTAKE_DISTANCE_TO_CUBE = 7.0; // centimeters
        INTAKE_TIMEOUT_JAWS = 0.5; // seconds
        INTAKE_TIMEOUT_WRIST = 0.5; //seconds
        INTAKE_INVERT_RIGHT = true;
        INTAKE_INVERT_LEFT = true;

        //                      Lift
        // _________________________________________________________________________________
        LIFT_MAX_POSSIBLE_UP = new double[] {(2100 + 2400) / 2.0, (2100 + 2400) / 2.0};  // DRL 3-16-18 15:1 ratio
        LIFT_MAX_POSSIBLE_DOWN = new double[] {LIFT_MAX_POSSIBLE_UP[0], LIFT_MAX_POSSIBLE_UP[1]};
        LIFT_MAX_POSSIBLE_VELOCITY = new double[] {LIFT_MAX_POSSIBLE_UP[0], LIFT_MAX_POSSIBLE_UP[1]};
        LIFT_POSIBLE_VELOCITY_PERCENTAGE = 0.975;
        LIFT_P = new double[] {11.0, 11.0};
        LIFT_I = new double[] {0.07, 0.07};
        LIFT_D = new double[] {70.0, 70.0};
        LIFT_F = new double[] {1023.0 / LIFT_MAX_POSSIBLE_UP[0], 1023.0 / LIFT_MAX_POSSIBLE_UP[1]};
        LIFT_VELOCITY = new int[] {(int) (LIFT_MAX_POSSIBLE_UP[0] * LIFT_POSIBLE_VELOCITY_PERCENTAGE), (int) (LIFT_MAX_POSSIBLE_UP[1] * LIFT_POSIBLE_VELOCITY_PERCENTAGE)};
        LIFT_ACCELERATION = new int[] {(int) (LIFT_VELOCITY[0] * 6.0), (int) (LIFT_VELOCITY[1] * 6.0)};

        LIFT_MOTOR_INVERTED_LEFT = true; // left
        LIFT_MOTOR_INVERTED_RIGHT = false; // right

        LIFT_SENSOR_PHASE_LEFT = true; // left
        LIFT_SENSOR_PHASE_RIGHT = true; // right

        LIFT_LIMIT_SWITCH_NORMALLY = new LimitSwitchNormal[][] {
            { LimitSwitchNormal.NormallyClosed, LimitSwitchNormal.NormallyOpen},  // Left Fwd
            { LimitSwitchNormal.NormallyOpen, LimitSwitchNormal.NormallyOpen}  // Right Fwd
        };

        //                      Tank
        // _________________________________________________________________________________
        TANK_MAX_VELOCITY = (12500 + 12750) / 2;
        TANK_P = 1.9;
        TANK_I = 0.005;
        TANK_D =  17.5;
        TANK_F = 1023 /  TANK_MAX_VELOCITY;
        TANK_CRUISE_VELOCITY = (int) (TANK_MAX_VELOCITY * 0.9);
        TANK_ACCELERATION = new int[] {(int) (TANK_CRUISE_VELOCITY * 0.825), (int) (TANK_CRUISE_VELOCITY * 0.825)};
        TANK_IZONE = 20;

        TANK_GEAR_RATIO_ENCODER_TO_WHEEL_HIGH = 24.0 / 40.0;
        TANK_GEAR_RATIO_ENCODER_TO_WHEEL_LOW = 40.0 / 24.0;

        TANK_LEFT_INVERT = true;
        TANK_RIGHT_INVERT = false;

        TANK_LEFT_SENSOR_PHASE = false;
        TANK_RIGHT_SENSOR_PHASE = false;

        TANK_OPEN_LOOP_RAMP = 0.5;

        //                      Winch
        // _________________________________________________________________________________
        WINCH = 22;
        WINCH_DOWN_IS_SAFE = true;
        WINCH_INVERT = true;
        WINCH_SPEED = 1;
    }
}
