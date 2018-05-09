package org.wfrobotics.robot.config.robotConfigs;

import org.wfrobotics.reuse.hardware.Gains;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public final class HerdVictor extends RobotConfig
{
    public HerdVictor()
    {
        //                      Intake
        // _________________________________________________________________________________
        INTAKE_SENSOR_R = 3;

        INTAKE_DISTANCE_TO_BUMPER = 17.22;  // centimeters
        INTAKE_DISTANCE_TO_CUBE = 16.6; // centimeters
        INTAKE_TIMEOUT_JAWS = 0.5; // seconds
        INTAKE_TIMEOUT_WRIST = 0.5; //seconds
        INTAKE_INVERT_RIGHT = true;
        INTAKE_INVERT_LEFT = true;

        //                      Lift
        // _________________________________________________________________________________
        LIFT_MAX_POSSIBLE_UP = 2200.0;  // DRL 3-19-18 First time different up and down gains
        LIFT_MAX_POSSIBLE_DOWN = 2900.0;
        LIFT_MAX_POSSIBLE_VELOCITY = LIFT_MAX_POSSIBLE_UP;
        LIFT_POSIBLE_VELOCITY_PERCENTAGE = 0.975;
        LIFT_P = new double[] {5.6, 0.0};
        LIFT_I = new double[] {0.001, 0.001};
        LIFT_D = new double[] {0.0, 0.0};
        LIFT_F = new double[] {1023.0 / LIFT_MAX_POSSIBLE_UP, 1023.0 / LIFT_MAX_POSSIBLE_DOWN};
        LIFT_VELOCITY = new int[] {(int) (LIFT_MAX_POSSIBLE_VELOCITY * LIFT_POSIBLE_VELOCITY_PERCENTAGE), (int) (LIFT_MAX_POSSIBLE_VELOCITY * LIFT_POSIBLE_VELOCITY_PERCENTAGE)};
        LIFT_ACCELERATION = new int[] {(int) (LIFT_VELOCITY[0] * 6.0), (int) (LIFT_VELOCITY[1] * 6.0)};

        LIFT_MOTOR_INVERTED_LEFT = true; // left
        LIFT_MOTOR_INVERTED_RIGHT = false; // right

        LIFT_SENSOR_PHASE_LEFT = true; // left
        LIFT_SENSOR_PHASE_RIGHT = true; // right

        LIFT_LIMIT_SWITCH_NORMALLY = new LimitSwitchNormal[][] {
            { LimitSwitchNormal.NormallyClosed, LimitSwitchNormal.NormallyClosed},  // Left Fwd
            { LimitSwitchNormal.NormallyClosed, LimitSwitchNormal.NormallyClosed}  // Right Fwd
        };

        //                      Tank
        // _________________________________________________________________________________
        TANK_MAX_VELOCITY = 11000.0;  // 12000 works way better than say 10500 at 9.9 ft/s DRL 3-16-18
        TANK_DISTANCE_P = 2.25;
        TANK_DISTANCE_I = 0.006;
        TANK_DISTANCE_D = 4.0;
        TANK_DISTANCE_F = 1023.0 /  TANK_MAX_VELOCITY;
        TANK_CRUISE_VELOCITY = (int) (1.0 * TANK_MAX_VELOCITY);
        TANK_ACCELERATION = new int[] {(int) (TANK_CRUISE_VELOCITY * 1.0), (int) (TANK_CRUISE_VELOCITY * 1.0)};
        TANK_DISTANCE_IZONE = 35;

        TANK_PATH_P = 0.07;
        TANK_PATH_I = 0.0;
        TANK_PATH_D = 0.315;
        TANK_PATH_IZONE = 35;

        TANK_PATH_VELOCITY_P = 1.0;
        TANK_PATH_VELOCITY_I = 0.0;
        TANK_PATH_VELOCITY_D = 5.0;
        TANK_PATH_VELOCITY_IZONE = 0;

        TANK_GEAR_RATIO_ENCODER_TO_WHEEL_HIGH = 24.0 / 40.0;
        TANK_GEAR_RATIO_ENCODER_TO_WHEEL_LOW = 40.0 / 24.0;

        TANK_LEFT_INVERT = false;
        TANK_RIGHT_INVERT = true;
        TANK_LEFT_SENSOR_PHASE = true;
        TANK_RIGHT_SENSOR_PHASE = false;
        TANK_OPEN_LOOP_RAMP = 0.05;

        //                      Winch
        // _________________________________________________________________________________
        WINCH = 22;
        WINCH_INVERT = true;
        WINCH_SPEED = 1;

        //                      Wrist
        // _________________________________________________________________________________
        WRIST_MAX_POSSIBLE_UP = 1310;  //(975.0 + 1310.0) / 2.0;
        WRIST_POSSIBLE_VELOCITY_PERCENTAGE = 0.975;
        WRIST_TICKS_TO_TOP = 4500;
        WRIST_GAINS = new Gains(0, 0.5, 0.00004, 0.0, 1023.0 / WRIST_MAX_POSSIBLE_UP, 0);
        WRIST_VELOCITY = (int) (WRIST_MAX_POSSIBLE_UP * WRIST_POSSIBLE_VELOCITY_PERCENTAGE);
        WRIST_ACCELERATION = WRIST_VELOCITY;
    }
}
