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
import org.wfrobotics.reuse.subsystems.control.Lookahead;
import org.wfrobotics.reuse.subsystems.control.PathFollower;
import org.wfrobotics.reuse.subsystems.control.PathFollower.Parameters;
import org.wfrobotics.reuse.subsystems.drive.TankMaths;

/** Robot Name: Herd Victor */
public class RobotConfig implements TankConfigSupplier
{
    private static RobotConfig instance = null;

    //                       Intake
    // _________________________________________________________________________________

    // Hardware
    public int kIntakeAddressL = 19;
    public int kIntakeAddressR = 20;
    public int kIntakeSolenoidF = 2;
    public int kIntakeSolenoidR = 3;
    public int kIntakeInfrared = 3;
    public boolean kIntakeInvertR = true;
    public boolean kIntakeInvertL = true;

    // Subsystem
    public static final int kIntakeBufferSize = 3;
    public static final double kIntakeDistanceSensorPluggedIn = 3000.0;  // TODO Tune me
    public static final double kIntakeDistanceReboot = kIntakeDistanceSensorPluggedIn * 3;
    public double kIntakeDistanceToCube = 6.5;
    public final double kJawsTimeoutSeconds = 0.5;

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
            new MasterConfig(10, false, true, new FollowerConfig(11, true, true)),
        }, new Gains[] {
            new Gains("Up", 0, 5.6, 0.001, 0.0, 1023.0 / kLiftVelocityMaxUp, 0, kLiftCruiseUp, kLiftAccelerationUp),
            new Gains("Down", 1, 0.0, 0.001, 0.0, 1023.0 / kLiftVelocityMaxUp, 0),
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

    //                      Winch
    // _________________________________________________________________________________
    public int kWinchAddress = 22;
    public boolean kWinchInvert = true;
    public double kWinchSpeed = 1.0;

    //                      Wrist
    // _________________________________________________________________________________

    // Hardware
    public PositionConfig getWristConfig()
    {
        int kTicksToTop = 5000;
        int kWristVelocityMax = 1100;
        int kWristVelocityCruise = (int) (kWristVelocityMax * 0.975);
        int kWristAcceleration = (int) (kWristVelocityCruise * 3.0);

        final PositionConfig c = new PositionConfig();

        c.kClosedLoop = new ClosedLoopConfig("Wrist", new MasterConfig[] {
            new MasterConfig(21, false, false),
        }, new Gains[] {
            new Gains("Motion Magic", 0, 0.5, 0.00004, 0.0, 1023.0 / kWristVelocityMax, 0, kWristVelocityCruise, kWristAcceleration),
        });
        c.kHardwareLimitNormallyOpenB = true;
        c.kHardwareLimitNormallyOpenT = true;
        c.kTicksToTop = kTicksToTop;
        c.kFullRangeInchesOrDegrees = 90.0;
        c.kSoftwareLimitB = Optional.of(-500);
        c.kSoftwareLimitT = Optional.of(kTicksToTop);
        c.kTuning = Optional.of(false);

        return c;
    }

    // Subsystem

    //                      Super Structure
    // _________________________________________________________________________________

    // Hardware

    // Subsystem

    //                      Tank
    // _________________________________________________________________________________

    // Hardware
    public TankConfig getTankConfig()
    {
        TankConfig config = new TankConfig();

        config.VELOCITY_MAX = 14000;
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.8);
        config.ACCELERATION = config.VELOCITY_PATH;
        config.STEERING_DRIVE_DISTANCE_P = 0.000022;
        config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        config.OPEN_LOOP_RAMP = 0.05;

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank", new MasterConfig[] {
            new MasterConfig(15, true, true, new FollowerConfig(17, false), new FollowerConfig(19, false)),
            new MasterConfig(14, false, true, new FollowerConfig(16, false), new FollowerConfig(18, false)),
        }, new Gains[] {
            new Gains("Motion Magic", 0, 2.25, 0.006, 4.0, 1023.0 / config.VELOCITY_MAX, 35, config.VELOCITY_PATH, config.ACCELERATION),
            new Gains("Velocity", 2, 0.175, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 20),
            // TODO Figure out if high acceleration > high PID?
            // TODO Turning PID only works if slot < 2. Thought there were 4??? Is it aux only?
            new Gains("Turn", 0, 4.0, 0.004, 18.0, 1023.0 / config.VELOCITY_MAX, 35, (int) (config.VELOCITY_MAX * 0.95), (int) (config.VELOCITY_MAX * 0.95)),
            //            new Gains("Turn", 0, 1.0, 0.001, 9.0, 1023.0 / config.VELOCITY_MAX, 35, (int) (config.VELOCITY_MAX
        });

        config.GEAR_RATIO_HIGH = 54.0 / 32.0;
        config.GEAR_RATIO_LOW = 54.0 / 32.0;
        config.SCRUB = 0.99;
        config.WHEEL_DIAMETER = 6.25;
        config.WIDTH = 24.0;

        return config;
    }

    public Parameters getPathConfig()
    {
        final double maxInchesPerSecond = Math.min(TankMaths.kVelocityMaxInchesPerSecond, 120.0);  // 120.0
        final double minLookahead = 12.0;  // Inches, 12.0
        final double maxLookahead = 24.0;  // Inches, 24.0
        final double minLookaheadSpeed = 9.0 / 120.0 * maxInchesPerSecond; // Inches/s, 9.0
        final double maxLookaheadSpeed = maxInchesPerSecond; //Inches/s, 120.0
        final double kInertiaSteering = 0.0;  // 0.0
        final double kP = 3.89;  // 5.0, Did our 10fps / their 18fps * their original on these three
        final double kI = 0.252;  // 0.03
        final double kV = 0.168;  // 0.02 TODO Investigate units, did we change in correct direction?
        final double kFeedForwardVelocity = 1.0;  // 1.0
        final double kFeedForwardAcceleration = 0.25;  // 0.05 TODO Investigate units, compare to characterization
        final double kCruiseVelocity = maxInchesPerSecond;  // Inches/s, 120.0
        final double kAcceleration = kCruiseVelocity; // Inches/s^2, 120.0
        final double kGoalTolPosition = 0.75;  // Inches within goal to finish MP, 0.75
        final double kGoalTolVelocity = 12.0;  // Inches/s within goal to finish MP, 12.0
        final double kStopSteeringDistance = 9.0;  // Inches
        final Lookahead lookahead = new Lookahead(minLookahead, maxLookahead, minLookaheadSpeed, maxLookaheadSpeed);
        return new PathFollower.Parameters(lookahead, kInertiaSteering, kP, kI, kV, kFeedForwardVelocity, kFeedForwardAcceleration, kCruiseVelocity, kAcceleration, kGoalTolPosition, kGoalTolVelocity, kStopSteeringDistance);
    }

    // Subsystem

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

