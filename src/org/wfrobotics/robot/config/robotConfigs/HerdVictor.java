package org.wfrobotics.robot.config.robotConfigs;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.config.TankConfig;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;

public final class HerdVictor extends RobotConfig
{
    public HerdVictor()
    {
        //                      Intake
        // _________________________________________________________________________________
        INTAKE_SENSOR_R = 3;

        INTAKE_DISTANCE_TO_CUBE = 16.6; // centimeters
        INTAKE_TIMEOUT_JAWS = 0.5; // seconds
        INTAKE_TIMEOUT_WRIST = 0.5; //seconds
        INTAKE_INVERT_RIGHT = true;
        INTAKE_INVERT_LEFT = true;

        //                      Lift
        // _________________________________________________________________________________
        double kLiftMaxUp = 2200.0;  // DRL 3-19-18 First time different up and down gains
        int kLiftCruiseUp = (int) (kLiftMaxUp * 0.975);  // TODO Gain schedule down motion magics?
        int kLiftAccelerationUp = (int) (kLiftCruiseUp * 6.0);

        LIFT_CLOSED_LOOP = new ClosedLoopConfig("Lift", new MasterConfig[] {
            new MasterConfig(10, false, true, new FollowerConfig(11, true, true)),
        }, new Gains[] {
            new Gains("Up", 0, 5.6, 0.001, 0.0, 1023.0 / kLiftMaxUp, 0, kLiftCruiseUp, kLiftAccelerationUp),
            new Gains("Down", 1, 0.0, 0.001, 0.0, 1023.0 / kLiftMaxUp, 0),
        });

        LIFT_LIMIT_SWITCH_NORMALLY = new LimitSwitchNormal[] {
            LimitSwitchNormal.NormallyClosed, LimitSwitchNormal.NormallyClosed
        };

        //                      Winch
        // _________________________________________________________________________________
        WINCH = 22;
        WINCH_INVERT = true;
        WINCH_SPEED = 1.0;

        //                      Wrist
        // _________________________________________________________________________________
        int kWristMax = 1310;  //(975.0 + 1310.0) / 2.0;
        int kWristCruiseVelocity = (int) (kWristMax * 0.975);
        int kWristAcceleration = kWristCruiseVelocity;

        WRIST_CLOSED_LOOP = new ClosedLoopConfig("Wrist", new MasterConfig[] {
            new MasterConfig(21, false, false),
        }, new Gains[] {
            new Gains("Motion Magic", 0, 0.5, 0.00004, 0.0, 1023.0 / kWristMax, 0, kWristCruiseVelocity, kWristAcceleration),
        });

        WRIST_DEADBAND = 0.1;
        WRIST_TICKS_TO_TOP = 5000;
    }

    public TankConfig getTankConfig()
    {
        TankConfig config = new TankConfig();

        config.VELOCITY_MAX = 10000.0;
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.8);
        config.ACCELERATION = config.VELOCITY_PATH;
        config.STEERING_DRIVE_DISTANCE_P = 0.000022;
        config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        config.OPEN_LOOP_RAMP = 0.05;

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank", new MasterConfig[] {
            new MasterConfig(15, false, true, new FollowerConfig(17, false), new FollowerConfig(19, false)),
            new MasterConfig(14, true, true, new FollowerConfig(16, false), new FollowerConfig(18, false)),
        }, new Gains[] {
            new Gains("Motion Magic", 0, 2.25, 0.006, 4.0, 1023.0 / config.VELOCITY_MAX, 35, config.VELOCITY_PATH, config.ACCELERATION),
            //            new Gains("Path", 1, 0.07, 0.0, 0.315, 0, 35),
            new Gains("Path", 1, 100.00, 0.0, 0.0, 0, 0),
            new Gains("Velocity", 2, 0.175, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 20),
            new Gains("Turn", 0, 2.25, 0.006, 4.0, 1023.0 / config.VELOCITY_MAX, 35, config.VELOCITY_PATH, config.ACCELERATION),
            // TODO Retune with increased turn derivative
        });

        config.GEAR_RATIO_HIGH = (36.0 / 15.0) * (24.0 / 40.0);
        config.GEAR_RATIO_LOW = (36.0 / 15.0) * (40.0 / 24.0);
        config.SCRUB = 0.96;
        config.WHEEL_DIAMETER = 6.25;
        config.WIDTH = 24.0;

        return config;
    }
}
