package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.TankConfig;

import java.util.Optional;

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

        config.VELOCITY_MAX = 3500.0 / 2;
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.2);
        config.ACCELERATION = config.VELOCITY_PATH ;
        config.STEERING_DRIVE_DISTANCE_P = 0.000022;
        config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        config.OPEN_LOOP_RAMP = 0.8; // how fast do you acellerate

        double TURN_SCALING = .35;

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank", new MasterConfig[] {
                // Left
                new MasterConfig(10, true, true, new FollowerConfig(12, false), new FollowerConfig(14, false)),
                // Right
                new MasterConfig(11, false, true, new FollowerConfig(13, false), new FollowerConfig(15, false)), },
                new Gains[] { new Gains("Velocity", 1, 0.0, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 0),
                        new Gains("Turn", 0, 1.0, 0.0000, 0.0 * 4.5, 1023.0 / config.VELOCITY_MAX, 0,
                                (int) (config.VELOCITY_MAX * TURN_SCALING), (int) (config.VELOCITY_MAX * TURN_SCALING)), });

        config.GEAR_RATIO_LOW = (54.0 / 32.0);
        config.SCRUB = 0.98;
        config.WHEEL_DIAMETER = 6  + 3.0 / 8.0;
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

             public PnuaticConfig getPnumaticConfig()
             {
                 final PnuaticConfig config = new PnuaticConfig();
                 
                     // Hardware
                      config.kAddressPCMGrippers = 0;
                      config.kAddressPCMShifter = 0;
                      config.kAddressPCMPoppers = 0;
     //   6,7
          // intake
                       config.kAddressSolenoidPoppersF = 0;
                       config.kAddressSolenoidPoppersB = 1;
                       // drive
                       config.kAddressSolenoidShifterF = 6;
                       config.kAddressSolenoidShifterB = 7;
          //climb
                       config.kAddressSolenoidGrippersF = 4;
                       config.kAddressSolenoidGrippersB = 5;
             //elevator
     
                       config.kAddressSolenoidLockersF = 0;
                       config.kAddressSolenoidLockersB = 1;
                       config.KAddressSolenoidPushUpF = 2;
                       config.KAddressSolenoidPushUpB = 3;
     
                 return config;
             }
    // Elevator
    // _________________________________________________________________________________

    // Hardware
    public PositionConfig getElevatorConfig() {
        int kTicksToTop = 137500;
        double kLiftVelocityMax = 12250.0;
        int kLiftCruiseUp = (int) (kLiftVelocityMax * 0.975);
        int kLiftAccelerationUp = (int) (kLiftCruiseUp * 3.50);

        final PositionConfig c = new PositionConfig();

        c.kClosedLoop = new ClosedLoopConfig("Lift",
                new MasterConfig[] { new MasterConfig(17, false, true, new FollowerConfig(16, true, false)) },
                new Gains[] { new Gains("Motion Magic", 0, 0.55, 0.0001, 0.6, 1023.0 / kLiftVelocityMax, 0,
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

    // Intake
    // _________________________________________________________________________________

    // Hardware
    public final double kIntakeDistanceTimeout = 0.025; // time in secounds 
    public final int kAddressTalonCargo = 8;
    public final boolean kInvertTalonCargo = true;

    // Link
    // _________________________________________________________________________________
    public PositionConfig getLinkConfig() {
        final PositionConfig c = new PositionConfig();

        // good 6500
        int kTicksToTop = 6500;
        int kLinkVelocityMax = 2100;
        int kLinkVelocityCruise = (int) (kLinkVelocityMax * 0.95);
        int kLinkAcceleration = (int) (kLinkVelocityCruise * 4.0);

        c.kClosedLoop = new ClosedLoopConfig("Link", new MasterConfig[] { new MasterConfig(9, true, false) },
                new Gains[] { new Gains("Motion Magic", 0, 6.0, 0.0000, 0.04, 1023.0 / kLinkVelocityMax, 0,
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
