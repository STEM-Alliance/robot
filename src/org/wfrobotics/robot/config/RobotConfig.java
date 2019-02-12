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
        config.OPEN_LOOP_RAMP = 0.15; // how fast do you acellerate

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank", new MasterConfig[] {
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            // Right
            new MasterConfig(18, true, true, new FollowerConfig(17,false), new FollowerConfig(16, false)),
            // Left
            new MasterConfig(13, false, true, new FollowerConfig(14, false), new FollowerConfig(15, false)),
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

    public class DeepSpaceTankConfig extends TankConfig
    {
        // TODO Drive Cheesy which accelerates slower when elevator is up
    }


    //                      Climb
    // _________________________________________________________________________________

    // Hardware
    public final int kAddressSolenoidGrippersF = 2;
    public final int kAddressSolenoidGrippersB = 3;


    //                       Elevator
    // _________________________________________________________________________________

    // Hardware
    public PositionConfig getElevatorConfig()
    {
        int kTicksToTop = Integer.MAX_VALUE;
        double kLiftVelocityMaxUp = 2200.0;
        int kLiftCruiseUp = (int) (kLiftVelocityMaxUp * 0.975);
        int kLiftAccelerationUp = (int) (kLiftCruiseUp * 6.0);

        final PositionConfig c = new PositionConfig();

        c.kClosedLoop = new ClosedLoopConfig("Lift", new MasterConfig[] {
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            new MasterConfig(10, false, true, new FollowerConfig(11, true, true),
                                            new FollowerConfig(12, true, true),
                                            new FollowerConfig(19, true, true)),
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        }, new Gains[] {
            new Gains("Motion Magic", 0, 0.0, 0.000, 0.0, 1023.0 / kLiftVelocityMaxUp, 0, kLiftCruiseUp, kLiftAccelerationUp),
        });
        c.kHardwareLimitNormallyOpenB = true;
        c.kHardwareLimitNormallyOpenT = true;
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 38.0;
        //        c.kSoftwareLimitB = Optional.of(-500);
        //        c.kSoftwareLimitT = Optional.of(kTicksToTop);
        //        c.kTuning = Optional.of(false);

        return c;
    }
    public final int kAddressSolenoidShifterF = 4;
    public final int kAddressSolenoidShifterB = 5;

    // Subsystem
    public static double kElevatorFeedForwardHasCube = 0.25;
    public static double kElevatorFeedForwardNoCube = 0.20;
    public static final int kElevatorTicksStartup = -1500;
    public static int kElevatorTickRateSlowVelocityObserved = 500;
    public static int kElevatorTickRateSlowEnough = kElevatorTickRateSlowVelocityObserved + 200;


    //                      Intake
    // _________________________________________________________________________________

    // Hardware
    public final int kAddressTalonCargo = 20;
    public final int kAddressTalonHatch = 21;
    public final int kAddressSolenoidPoppersF = 0;
    public final int kAddressSolenoidPoppersB = 1;
    public final int kAddressDigitalHatchSensor = 0;

    //                      Link
    // _________________________________________________________________________________
    public PositionConfig getLinkConfig()
    {
        final PositionConfig c = new PositionConfig();

        int kTicksToTop = 7000;
        int kWristVelocityMax = 540;
        int kWristVelocityCruise = (int) (kWristVelocityMax * 0.975);
        int kWristAcceleration = (int) (kWristVelocityCruise * 6.0);

        c.kClosedLoop = new ClosedLoopConfig("Link", new MasterConfig[] {
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            new MasterConfig(8, true, true)
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }, new Gains[] {
            new Gains("Motion Magic", 0, 1.0, 0.0000, 0.0, 1023.0 / kWristVelocityMax, 0, kWristVelocityCruise, kWristAcceleration),
        });
        c.kHardwareLimitNormallyOpenB = true;
        c.kHardwareLimitNormallyOpenT = true;
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 90.0;
        //        c.kSoftwareLimitB = Optional.of(-500);
        c.kSoftwareLimitT = Optional.of(kTicksToTop);
        //        c.kTuning = Optional.of(true);

        return c;
    }

    // Subsystem
    public static double kLinkTopPosition = 90.0;


    //                       Wrist
    // _________________________________________________________________________________
    public PositionConfig getWristConfig()
    {
        final PositionConfig c = new PositionConfig();

        int kTicksToTop = 99999;
        int kWristVelocityMax = 99999;
        int kWristVelocityCruise = (int) (kWristVelocityMax * 0.975);
        int kWristAcceleration = (int) (kWristVelocityCruise * 6.0);

        c.kClosedLoop = new ClosedLoopConfig("Wrist", new MasterConfig[] {
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            new MasterConfig(9, false, false)
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        }, new Gains[] {
            new Gains("Motion Magic", 0, 0.0, 0.0000, 0.0, 1023.0 / kWristVelocityMax, 0, kWristVelocityCruise, kWristAcceleration),
        });
        c.kHardwareLimitNormallyOpenB = true;
        c.kHardwareLimitNormallyOpenT = true;
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 90.0;
        //        c.kSoftwareLimitB = Optional.of(-500);
        //        c.kSoftwareLimitT = Optional.of(kTicksToTop);
        //        c.kTuning = Optional.of(true);

        return c;
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

