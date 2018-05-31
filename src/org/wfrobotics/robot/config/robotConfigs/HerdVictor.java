package org.wfrobotics.robot.config.robotConfigs;

import org.wfrobotics.reuse.hardware.Gains;
import org.wfrobotics.reuse.subsystems.drive.TankConfig;

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
        LIFT_GAINS = new Gains[] {
            new Gains(0, 5.6, 0.001, 0.0, 1023.0 / LIFT_MAX_POSSIBLE_UP, 0),
            new Gains(1, 0.0, 0.001, 0.0, 1023.0 / LIFT_MAX_POSSIBLE_DOWN, 0),
        };
        LIFT_VELOCITY = new int[] {(int) (LIFT_MAX_POSSIBLE_VELOCITY * LIFT_POSIBLE_VELOCITY_PERCENTAGE), (int) (LIFT_MAX_POSSIBLE_VELOCITY * LIFT_POSIBLE_VELOCITY_PERCENTAGE)};
        LIFT_ACCELERATION = new int[] {(int) (LIFT_VELOCITY[0] * 6.0), (int) (LIFT_VELOCITY[1] * 6.0)};

        LIFT_MOTOR_INVERTED_LEFT = true; // left
        LIFT_MOTOR_INVERTED_RIGHT = false; // right

        LIFT_SENSOR_PHASE_LEFT = true; // left
        LIFT_SENSOR_PHASE_RIGHT = true; // right

        LIFT_LIMIT_SWITCH_NORMALLY = new LimitSwitchNormal[][] {
            { LimitSwitchNormal.NormallyClosed, LimitSwitchNormal.NormallyClosed},  // Left Fwd
            { LimitSwitchNormal.NormallyClosed, LimitSwitchNormal.NormallyClosed},  // Right Fwd
        };

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

    public TankConfig getTankConfig()
    {
        TankConfig config = new TankConfig();

        config.MASTER_L = 15;
        config.MASTER_R = 16;
        config.FOLLOWERS_L = new int[] { 17 };
        config.FOLLOWERS_R = new int[] { 14 };

        config.VELOCITY_MAX = 11000.0;  // 12000 works way better than say 10500 at 9.9 ft/s DRL 3-16-18
        config.VELOCITY_PATH = (int) (1.0 * config.VELOCITY_MAX);
        config.ACCELERATION = new int[] {(int) (config.VELOCITY_PATH * 1.0), (int) (config.VELOCITY_PATH * 1.0)};
        config.OPEN_LOOP_RAMP = 0.05;

        config.GAINS_DISTANCE = new Gains(0, 2.25, 0.006, 4.0, 1023.0 / config.VELOCITY_MAX, 35);
        config.GAINS_PATH = new Gains(1, 0.07, 0.0, 0.315, 0, 35);
        config.GAINS_PATH_VELOCITY = new Gains(2, 1.0, 0.0, 5.0, 0, 0);
        config.GAINS_GYRO = new Gains(3, 2.0, 0.0, 0.0, 0.0, 0);

        config.GEAR_RATIO_HIGH = (36.0 / 15.0) * (24.0 / 40.0);
        config.GEAR_RATIO_LOW = (36.0 / 15.0) * (40.0 / 24.0);
        config.SCRUB = 0.96;
        config.WHEEL_DIAMETER = 6.25;
        config.WIDTH = 24.0;

        config.INVERT_L = false;
        config.INVERT_R = true;
        config.SENSOR_PHASE_L = true;
        config.SENSOR_PHASE_R = false;

        return config;
    }
}
