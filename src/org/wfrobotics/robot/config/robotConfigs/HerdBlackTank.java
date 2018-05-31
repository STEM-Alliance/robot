package org.wfrobotics.robot.config.robotConfigs;

import org.wfrobotics.reuse.hardware.Gains;
import org.wfrobotics.reuse.subsystems.drive.TankConfig;

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
        LIFT_GAINS = new Gains[] {
            new Gains(0, 0.0, 0.0, 0.0, 1023.0 / LIFT_MAX_POSSIBLE_UP, 0),
            new Gains(1, 0.0, 0.0, 0.0, 1023.0 / LIFT_MAX_POSSIBLE_DOWN, 0),
        };
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

        //                      Winch
        // _________________________________________________________________________________
        WINCH = 22;
        WINCH_INVERT = true;
        WINCH_SPEED = 1;
    }

    public TankConfig getTankConfig()
    {
        TankConfig config = new TankConfig();

        config.MASTER_L = 15;
        config.MASTER_R = 16;
        config.FOLLOWERS_L = new int[] { 17 };
        config.FOLLOWERS_R = new int[] { 14 };

        config.VELOCITY_MAX = 10000.0;  // 2-28-18
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * .9);
        config.ACCELERATION = new int[] {config.VELOCITY_PATH, config.VELOCITY_PATH};
        config.OPEN_LOOP_RAMP = 0.05;

        config.GAINS_DISTANCE = new Gains(0, 0.31, 0.005, 0.68, 1023.0 / config.VELOCITY_MAX, 20);
        config.GAINS_PATH = new Gains(0, 0.1625, 0.005, 0.65, 0.0, 35);
        config.GAINS_PATH_VELOCITY = new Gains(2, 1.0, 0.0, 5.0, 0, 0);
        config.GAINS_GYRO = new Gains(3, 2.0, 0.0, 0.0, 0.0, 0);

        config.GEAR_RATIO_HIGH = (36.0 / 15.0) * (24.0 / 60.0);
        config.GEAR_RATIO_LOW = (36.0 / 15.0) * (60.0 / 24.0);
        config.SCRUB = 0.96;
        config.WHEEL_DIAMETER = 6.25;
        config.WIDTH = 24.0;

        config.INVERT_L = true;
        config.INVERT_R = false;
        config.SENSOR_PHASE_L = false;
        config.SENSOR_PHASE_R = true;

        return config;
    }
}
