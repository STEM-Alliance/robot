package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.EnhancedRobotConfig;
import java.util.Optional;

import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.config.TankConfig;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem.PositionConfig;

import edu.wpi.first.wpilibj.command.Command;

public class RobotConfig extends EnhancedRobotConfig
{
    private static RobotConfig instance = null;

     // Tank
    // _________________________________________________________________________________
    public TankConfig getTankConfig() 
    {
        final TankConfig config = new TankConfig();

        // config.CLOSED_LOOP_ENABLED = false;  // TODO remove after making closed loop an Optional

        // config.VELOCITY_MAX = 3500.0 / 2;
        // config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.85);
        // config.ACCELERATION = config.VELOCITY_PATH ;
        // config.STEERING_DRIVE_DISTANCE_P = 0.000022;  // TODO Make drive distance a Optional and its owm config
        // config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        // config.OPEN_LOOP_RAMP = 0.05; // how fast do you acellerate
        // config.MAX_PERCENT_OUT = 1.0;

        // double TURN_SCALING = .35;

        // // TODO Make closed loop an Optional

        // config.CLOSED_LOOP = new ClosedLoopConfig("Tank", 
        //     new MasterConfig[] {
        //         // Left
        //         new MasterConfig(10, true, true, new FollowerConfig(12, false), new FollowerConfig(14, false)),
        //         // Right
        //         new MasterConfig(11, false, true, new FollowerConfig(13, false), new FollowerConfig(15, false)),
        //     },
        //     new Gains[] {
        //         new Gains("Velocity", 1, 0.0, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 100),
        //         new Gains("Turn", 0, 1.0, 0.0000, 0.0 * 4.5, 1023.0 / config.VELOCITY_MAX, 100,
        //                         (int) (config.VELOCITY_MAX * TURN_SCALING), (int) (config.VELOCITY_MAX * TURN_SCALING)),
        //     }
        // );

        // config.GEAR_RATIO_LOW = (54.0 / 32.0);
        // config.SCRUB = 0.98;
        // config.WHEEL_DIAMETER = 6 + 3.0 / 8.0;
        // config.WIDTH = 27.0;

        return config;
    }

    // public class DeepSpaceTankConfig extends TankConfig 
    // {
    //     @Override
    //     public Command getTeleopCommand()
    //     {
    //         return new DriveToTarget();
    //     }
    // }
    // Climb
    // _________________________________________________________________________________

    // Constructor
    // _________________________________________________________________________________
    protected RobotConfig()
    {
        cameraStream = Optional.of(false);
    }

    //  Helper Methods
    // _________________________________________________________________________________

    public static RobotConfig getInstance()
    {
        if (instance == null)
        {
            instance = (RobotConfig) RobotConfigPicker.get(new EnhancedRobotConfig[] {
                new RobotConfig(),     // Competition robot
                new PracticeConfig(),  // Practice robot difference
            });
        }
        return instance;
    }
}