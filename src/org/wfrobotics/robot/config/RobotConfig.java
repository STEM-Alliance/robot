package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.config.TankConfig;
import org.wfrobotics.reuse.config.TankConfig.TankConfigSupplier;

public class RobotConfig implements TankConfigSupplier
{
    private static RobotConfig instance = null;


    //                      Tank
    // _________________________________________________________________________________

    // Hardware
    public TankConfig getTankConfig()
    {
        TankConfig config = new DeepSpaceTankConfig();

        config.VELOCITY_MAX = 6250.0;
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.8);
        config.ACCELERATION = config.VELOCITY_PATH;
        config.STEERING_DRIVE_DISTANCE_P = 0.000022;
        config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        config.OPEN_LOOP_RAMP = 0.05; // how fast do you acellerate

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank", new MasterConfig[] {
            // Right
            new MasterConfig(15, true, true, new FollowerConfig(17,false), new FollowerConfig(19, false)),
            // Left
            new MasterConfig(14, false, true, new FollowerConfig(18, false), new FollowerConfig(16, false)),

        }, new Gains[] {
            new Gains("Velocity", 1, 0.0, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 0),
            new Gains("Turn", 0, 0.175, 0.0004, 0.175 * 4.5 , 1023.0 / config.VELOCITY_MAX, 0, (int) (config.VELOCITY_MAX * 0.95), (int) (config.VELOCITY_MAX * 0.95)),
        });

        config.GEAR_RATIO_HIGH = (54.0 / 32.0);
        config.GEAR_RATIO_LOW = (54.0 / 32.0);
        config.SCRUB = 0.98;
        config.WHEEL_DIAMETER = 6 + 3/8;
        config.WIDTH = 27.0;

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
            instance = RobotConfigPicker.get(new RobotConfig[] {
                new RobotConfig(),     // Competition robot
                new PracticeConfig(),  // Practice robot differences
            });
        }
        return instance;
    }
}

