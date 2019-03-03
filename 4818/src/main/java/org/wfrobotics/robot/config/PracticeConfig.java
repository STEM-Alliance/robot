package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.TankConfig;

import java.util.Optional;

import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem.PositionConfig;

/** Practice Robot Config. Override any settings that differ here */
public final class PracticeConfig extends RobotConfig {
    // Tank
    // _________________________________________________________________________________

    // Hardware
    public TankConfig getTankConfig() {
        final TankConfig config = new DeepSpaceTankConfig();

        config.VELOCITY_MAX = 6250.0;
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.8);
        config.ACCELERATION = config.VELOCITY_PATH;
        config.STEERING_DRIVE_DISTANCE_P = 0.000022;
        config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        config.OPEN_LOOP_RAMP = 0.30; // how fast do you acellerate

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank", new MasterConfig[] {
                // Left
                new MasterConfig(10, true, true, new FollowerConfig(12, false), new FollowerConfig(14, false)),
                // Right
                new MasterConfig(11, false, true, new FollowerConfig(13, false), new FollowerConfig(15, false)), },
                new Gains[] { new Gains("Velocity", 1, 0.0, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 0),
                        new Gains("Turn", 0, 0.175, 0.0004, 0.175 * 4.5, 1023.0 / config.VELOCITY_MAX, 0,
                                (int) (config.VELOCITY_MAX * 0.95), (int) (config.VELOCITY_MAX * 0.95)), });

        config.GEAR_RATIO_LOW = (54.0 / 32.0);
        config.SCRUB = 0.98;
        config.WHEEL_DIAMETER = 6 + 3 / 8;
        config.WIDTH = 27.0;

        return config;
    }

    public class DeepSpaceTankConfig extends TankConfig {
        // @Override
        // public Command getTeleopCommand()
        // {
        // return new DriveCheesy(); // TODO DriveCarefully, accelerates slower when
        // elevator is up
        // }
    }

    // Climb
    // _________________________________________________________________________________

    // Hardware
    public final int kAddressPCMGrippers = 0;
    public final int kAddressSolenoidGrippersF = 2;
    public final int kAddressSolenoidGrippersB = 3;

    // Elevator
    // _________________________________________________________________________________

    // Hardware
    public PositionConfig getElevatorConfig() {
        int kTicksToTop = 137500;
        double kLiftVelocityMaxUp = 2200.0;
        int kLiftCruiseUp = (int) (kLiftVelocityMaxUp * 0.975);
        int kLiftAccelerationUp = (int) (kLiftCruiseUp * 6.0);

        final PositionConfig c = new PositionConfig();

        c.kClosedLoop = new ClosedLoopConfig("Lift",
                new MasterConfig[] { new MasterConfig(17, false, true, new FollowerConfig(16, true, false)) },
                new Gains[] { new Gains("Motion Magic", 0, 0.0, 0.000, 0.0, 1023.0 / kLiftVelocityMaxUp, 0,
                        kLiftCruiseUp, kLiftAccelerationUp), });
        c.kHardwareLimitNormallyOpenB = true;
        c.kHardwareLimitNormallyOpenT = true;
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 68.5;
        c.kSoftwareLimitT = Optional.of(kTicksToTop);
        c.kSoftwareLimitB = Optional.of(100);
        // c.kTuning = Optional.of(false);

        return c;
    }

    public final int kAddressPCMShifter = 0;
    public final int kAddressSolenoidShifterF = 4;
    public final int kAddressSolenoidShifterB = 5;

    // Intake
    // _________________________________________________________________________________

    // Hardware
    public final int kAddressTalonCargo = 8;
    public final boolean kInvertTalonCargo = true;
    public final int kAddressPCMPoppers = 0;
    public final int kAddressSolenoidPoppersF = 0;
    public final int kAddressSolenoidPoppersB = 1;
    public final int kAddressDigitalHatchSensor = 0;

    // Link
    // _________________________________________________________________________________
    public PositionConfig getLinkConfig() {
        final PositionConfig c = new PositionConfig();

        // good 6500
        int kTicksToTop = 6750;
        int kLinkVelocityMax = 1100;
        int kLinkVelocityCruise = (int) (kLinkVelocityMax * 0.975);
        int kLinkAcceleration = (int) (kLinkVelocityCruise * 6.0);

        c.kClosedLoop = new ClosedLoopConfig("Link", new MasterConfig[] { new MasterConfig(9, false, false) },
                new Gains[] { new Gains("Motion Magic", 0, 25.5, 0.0004, 0.08, 1023.0 / kLinkVelocityMax, 0,
                        kLinkVelocityCruise, kLinkAcceleration), });
        c.kHardwareLimitNormallyOpenB = true;
        c.kHardwareLimitNormallyOpenT = true;
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 100.0;
        c.kSoftwareLimitT = Optional.of(kTicksToTop);
        // c.kTuning = Optional.of(true);

        return c;
    }

    // Constructor
    // protected RobotConfig()
    // {
    // this.vision = Optional.of(new VisionConfig(69.0));
    // }

}
