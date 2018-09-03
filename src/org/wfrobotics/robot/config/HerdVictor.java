package org.wfrobotics.robot.config;

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
        // ------------------------ Intake ------------------------

        kIntakeInfrared = 3;

        kIntakeDistanceToCube = 85.0;
        kJawsTimeoutSeconds = 0.5; //seconds
        kIntakeInvertR = true;
        kIntakeInvertL = true;

        // ------------------------- Lift -------------------------

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

        // ------------------------ Winch -------------------------

        kWinchAddress = 22;
        kWinchInvert = true;
        kWinchSpeed = 1.0;

        // ------------------------ Wrist -------------------------

        int kWristMax = 1100;
        int kWristCruiseVelocity = (int) (kWristMax * 0.975);
        int kWristAcceleration = (int) (kWristCruiseVelocity * 3.0);

        WRIST_CLOSED_LOOP = new ClosedLoopConfig("Wrist", new MasterConfig[] {
            new MasterConfig(21, false, false),
        }, new Gains[] {
            new Gains("Motion Magic", 0, 0.5, 0.00004, 0.0, 1023.0 / kWristMax, 0, kWristCruiseVelocity, kWristAcceleration),
        });

        kWristDeadband = 0.1;
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
            new MasterConfig(15, false, true, new FollowerConfig(17, true)),
            new MasterConfig(16, true, true, new FollowerConfig(14, true)),
        }, new Gains[] {
            new Gains("Motion Magic", 0, 2.25, 0.006, 4.0, 1023.0 / config.VELOCITY_MAX, 35, config.VELOCITY_PATH, config.ACCELERATION),
            new Gains("Path", 1, 100.00, 0.0, 0.0, 0, 0),
            new Gains("Velocity", 2, 0.175, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 20),
            // TODO Figure out if high acceleration > high PID?
            new Gains("Turn", 3, 4.0, 0.004, 18.0, 1023.0 / config.VELOCITY_MAX, 35, (int) (config.VELOCITY_MAX * 0.95), (int) (config.VELOCITY_MAX * 0.95)),
            //            new Gains("Turn", 3, 1.0, 0.001, 4.5, 1023.0 / config.VELOCITY_MAX, 20, (int) (config.VELOCITY_MAX * 0.95), (int) (config.VELOCITY_MAX * 3.0)),
        });

        config.GEAR_RATIO_HIGH = (36.0 / 15.0) * (24.0 / 40.0);
        config.GEAR_RATIO_LOW = (36.0 / 15.0) * (40.0 / 24.0);
        config.SCRUB = 0.96;
        config.WHEEL_DIAMETER = 6.25;
        config.WIDTH = 24.0;

        return config;
    }
}
