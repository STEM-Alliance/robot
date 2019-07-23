package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.TankConfig;

import java.util.Optional;

import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem.PositionConfig;

import edu.wpi.first.wpilibj.command.Command;

/** Practice Robot Config. Override any settings that differ here */
public final class PracticeConfig extends RobotConfig
{
    // Tank
    // _________________________________________________________________________________
    public TankConfig getTankConfig()
    {
        final TankConfig config = new TankConfig();
        
        // config.CLOSED_LOOP_ENABLED = false;

        // config.VELOCITY_MAX = 3500.0 / 2;
        // config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.2);
        // config.ACCELERATION = config.VELOCITY_PATH ;
        // config.STEERING_DRIVE_DISTANCE_P = 0.000022;
        // config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        // config.OPEN_LOOP_RAMP = 0.05; // how fast do you acellerate
        // config.MAX_PERCENT_OUT = 0.85;

        // double TURN_SCALING = .35;

        // config.CLOSED_LOOP = new ClosedLoopConfig("Tank",
        //     new MasterConfig[] {
        //         // Left
        //         new MasterConfig(10, true, true, new FollowerConfig(12, false), new FollowerConfig(14, false)),
        //         // Right
        //         new MasterConfig(11, false, true, new FollowerConfig(13, false), new FollowerConfig(15, false)),
        //     },
        //     new Gains[] { 
        //         new Gains("Velocity", 1, 0.0, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 0),
        //         new Gains("Turn", 0, 1.0, 0.0000, 0.0 * 4.5, 1023.0 / config.VELOCITY_MAX, 0,
        //                       (int) (config.VELOCITY_MAX * TURN_SCALING), (int) (config.VELOCITY_MAX * TURN_SCALING)),
        //     }
        // );

        // config.GEAR_RATIO_LOW = (54.0 / 32.0);
        // config.SCRUB = 0.98;
        // config.WHEEL_DIAMETER = 6  + 3.0 / 8.0;
        // config.WIDTH = 27.0;

        return config;
    }


    // Constructor
    // _________________________________________________________________________________
    protected PracticeConfig()
    {
        cameraStream = Optional.of(false);
    }
}
