package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.EnhancedRobotConfig;
import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.config.TankConfig;

public class RobotConfig extends EnhancedRobotConfig
{
    private static RobotConfig instance = null;


    //                      Tank
    // _________________________________________________________________________________

    // Hardware
    public TankConfig getTankConfig()
    {
        TankConfig config = new DeepSpaceTankConfig();

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
            new Gains("Velocity", 2, 0.175, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 20),
            // TODO Figure out if high acceleration > high PID?
            // TODO Turning PID only works if slot < 2. Thought there were 4??? Is it aux only?
            new Gains("Turn", 0, 4.0, 0.004, 18.0, 1023.0 / config.VELOCITY_MAX, 35, (int) (config.VELOCITY_MAX * 0.95), (int) (config.VELOCITY_MAX * 0.95)),
            //            new Gains("Turn", 0, 1.0, 0.001, 9.0, 1023.0 / config.VELOCITY_MAX, 35, (int) (config.VELOCITY_MAX
        });

        config.GEAR_RATIO_LOW = (36.0 / 15.0) * (40.0 / 24.0);
        config.SCRUB = 0.96;
        config.WHEEL_DIAMETER = 6.25;
        config.WIDTH = 24.0;

        return config;
    }

    public class DeepSpaceTankConfig extends TankConfig
    {

    }

    //                      Helper Methods
    // _________________________________________________________________________________

    public static RobotConfig getInstance()
    {
        if (instance == null)
        {
            instance = (RobotConfig) RobotConfigPicker.get(new EnhancedRobotConfig[] {
                new RobotConfig(), // Competition robot
                new PracticeConfig(), // Practice robot differences
            });
        }
        return instance;
    }
}

