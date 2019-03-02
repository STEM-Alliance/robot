package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.config.TankConfig;

public final class Respawn extends RobotConfig
{
    public Respawn()
    {
        //                      Intake
        // _________________________________________________________________________________

        //                      Lift
        // _________________________________________________________________________________
       
        //                      Winch
        // _________________________________________________________________________________
       
        //                      Wrist
        // _________________________________________________________________________________
    }

    public TankConfig getTankConfig()
    {
        TankConfig config = new TankConfig();

        config.VELOCITY_MAX = 3100.0;
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.8);
        config.ACCELERATION = config.VELOCITY_PATH;
        config.STEERING_DRIVE_DISTANCE_P = 0.000022;
        config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        config.OPEN_LOOP_RAMP = 0.05;

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank", new MasterConfig[] {
//            new MasterConfig(15, false, true, new FollowerConfig(17, true)),
//            new MasterConfig(16, true, true, new FollowerConfig(14, true)),
              new MasterConfig(15, false, true),
              new MasterConfig(16, true, true),
        }, new Gains[] {
            new Gains("Turn", 0, 0, 0, 0, 1023.0 / config.VELOCITY_MAX, 35, config.VELOCITY_PATH, config.ACCELERATION),
            //            new Gains("Path", 1, 0.07, 0.0, 0.315, 0, 35),
            new Gains("Velocity", 2, 0.05, 0.0, 0, 1023.0 / config.VELOCITY_MAX, 0),
        });

        config.GEAR_RATIO_HIGH = 1; // (36.0 / 15.0) * (24.0 / 40.0);
        config.GEAR_RATIO_LOW = 1; // (36.0 / 15.0) * (40.0 / 24.0);
        config.SCRUB = 0.96;
        config.WHEEL_DIAMETER = 6.25;
        config.WIDTH = 24.0;

        return config;
    }
}
