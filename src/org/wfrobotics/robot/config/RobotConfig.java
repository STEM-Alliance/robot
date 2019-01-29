package org.wfrobotics.robot.config;

import java.util.Optional;

import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.config.TankConfig;
import org.wfrobotics.reuse.config.TankConfig.TankConfigSupplier;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem.PositionConfig;

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
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // Right
            new MasterConfig(15, true, true, new FollowerConfig(17,false), new FollowerConfig(19, false)),
            // Left
            new MasterConfig(14, false, true, new FollowerConfig(18, false), new FollowerConfig(16, false)),
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
    //                      Link
    // _________________________________________________________________________________
    public PositionConfig getLinkConfig()
    {
        PositionConfig config = new PositionConfig();

        int kTicksToTop = 5000;
        int kWristVelocityMax = 1100;
        int kWristVelocityCruise = (int) (kWristVelocityMax * 0.975);
        int kWristAcceleration = (int) (kWristVelocityCruise * 3.0);

        final PositionConfig c = new PositionConfig();

        c.kClosedLoop = new ClosedLoopConfig("Link", new MasterConfig[] {
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            new MasterConfig(21, false, false, new FollowerConfig(22, true))
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }, new Gains[] {
            new Gains("Motion Magic", 0, 0.0, 0.0000, 0.0, 1023.0 / kWristVelocityMax, 0, kWristVelocityCruise, kWristAcceleration),
        });
        c.kHardwareLimitNormallyOpenB = true;
        c.kHardwareLimitNormallyOpenT = true;
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 90.0;
        c.kSoftwareLimitB = Optional.of(-500);
        c.kSoftwareLimitT = Optional.of(kTicksToTop);
        c.kTuning = Optional.of(false);

        return config;
    }

    //                       Lift
    // _________________________________________________________________________________

    // Hardware
    public PositionConfig getLiftConfig()
    {
        int kTicksToTop = 27000;
        double kLiftVelocityMaxUp = 2200.0;
        int kLiftCruiseUp = (int) (kLiftVelocityMaxUp * 0.975);
        int kLiftAccelerationUp = (int) (kLiftCruiseUp * 6.0);

        final PositionConfig c = new PositionConfig();

        c.kClosedLoop = new ClosedLoopConfig("Lift", new MasterConfig[] {
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            new MasterConfig(10, false, true, new FollowerConfig(11, true, true)),
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        }, new Gains[] {
            new Gains("Motion Magic", 0, 0.0, 0.000, 0.0, 1023.0 / kLiftVelocityMaxUp, 0, kLiftCruiseUp, kLiftAccelerationUp),
        });
        c.kHardwareLimitNormallyOpenB = false;
        c.kHardwareLimitNormallyOpenT = false;
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 38.0;
        c.kSoftwareLimitB = Optional.of(-500);
        c.kSoftwareLimitT = Optional.of(kTicksToTop);
        c.kTuning = Optional.of(false);

        return c;
    }

    // Subsystem
    public static double kLiftFeedForwardHasCube = 0.25;
    public static double kLiftFeedForwardNoCube = 0.20;
    public static final int kLiftTicksStartup = -1500;
    public static int kLiftTickRateSlowVelocityObserved = 500;
    public static int kLiftTickRateSlowEnough = kLiftTickRateSlowVelocityObserved + 200;


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

