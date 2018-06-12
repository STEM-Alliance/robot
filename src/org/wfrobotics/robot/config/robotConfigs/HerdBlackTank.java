package org.wfrobotics.robot.config.robotConfigs;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
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

        LIFT_CLOSED_LOOP = new ClosedLoopConfig("Tank", new MasterConfig[] {
            new MasterConfig(11, true, true),
            new MasterConfig(10, false, true),
        }, new Gains[] {
            new Gains("Up", 0, 0.0, 0.0, 0.0, 1023.0 / 1.0, 0),
            new Gains("Down", 1, 0.0, 0.0, 0.0, 1023.0 / 1.0, 0),
        });

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

        config.DEBUG = false;

        config.FOLLOWERS_L = new int[] { 17 };
        config.FOLLOWERS_R = new int[] { 14 };

        config.VELOCITY_MAX = 10000.0;  // 2-28-18
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * .9);
        config.ACCELERATION = config.VELOCITY_PATH;
        config.OPEN_LOOP_RAMP = 0.05;

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank", new MasterConfig[] {
            new MasterConfig(15, true, false),
            new MasterConfig(16, false, true),
        }, new Gains[] {
            new Gains("Motion Magic", 0, 0.31, 0.005, 0.68, 1023.0 / config.VELOCITY_MAX, 20, config.VELOCITY_PATH, config.ACCELERATION),
            new Gains("Path", 1, 0.1625, 0.005, 0.65, 0.0, 35),
            new Gains("Velocity", 2, 1.0, 0.0, 5.0, 0, 0),
            new Gains("Gyro", 3, 2.0, 0.0, 0.0, 0.0, 0),
        });

        config.GEAR_RATIO_HIGH = (36.0 / 15.0) * (24.0 / 60.0);
        config.GEAR_RATIO_LOW = (36.0 / 15.0) * (60.0 / 24.0);
        config.SCRUB = 0.96;
        config.WHEEL_DIAMETER = 6.25;
        config.WIDTH = 24.0;

        return config;
    }
}
