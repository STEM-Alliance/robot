package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.config.EnhancedRobotConfig;
import org.wfrobotics.reuse.config.RobotConfigPicker;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TalonConfig.FollowerConfig;
import org.wfrobotics.reuse.config.TalonConfig.Gains;
import org.wfrobotics.reuse.config.TalonConfig.MasterConfig;
import org.wfrobotics.reuse.config.TankConfig;
import org.wfrobotics.reuse.config.TankConfig.TankConfigSupplier;
import org.wfrobotics.reuse.subsystems.control.Lookahead;
import org.wfrobotics.reuse.subsystems.control.PathFollower;
import org.wfrobotics.reuse.subsystems.control.PathFollower.Parameters;
import org.wfrobotics.reuse.subsystems.drive.TankMaths;

public class RobotConfig extends EnhancedRobotConfig
{
    private static RobotConfig instance = null;

    public final int CAN_PNEUMATIC_CONTROL_MODULE = 7;

    //                      Intake
    // _________________________________________________________________________________

    //                      Lift
    // _________________________________________________________________________________


    //                      Winch
    // _________________________________________________________________________________


    //                      Wrist
    // _________________________________________________________________________________

    public TankConfig getTankConfig()
    {
        TankConfig config = new TankConfig();

        config.VELOCITY_MAX = 10750.0;
        config.VELOCITY_PATH = (int) (config.VELOCITY_MAX * 0.8);
        config.ACCELERATION = config.VELOCITY_PATH;
        config.STEERING_DRIVE_DISTANCE_P = 0.000022;
        config.STEERING_DRIVE_DISTANCE_I = 0.000005;
        config.OPEN_LOOP_RAMP = 0.05;

        config.CLOSED_LOOP = new ClosedLoopConfig("Tank", new MasterConfig[] {
            // new MasterConfig(11, false, true, new FollowerConfig(10, true), new FollowerConfig(12, false)),
            // new MasterConfig(14, true, true, new FollowerConfig(13, false), new FollowerConfig(15, true)),
            new MasterConfig(17, false, false, new FollowerConfig(10, false)),
            new MasterConfig(11, true, false, new FollowerConfig(22, false)),
        }, new Gains[] {
            new Gains("Motion Magic", 0, 2.25, 0.006, 4.0, 1023.0 / config.VELOCITY_MAX, 35, config.VELOCITY_PATH, config.ACCELERATION),
            new Gains("Velocity", 2, 0.175, 0.0, 0.0, 1023.0 / config.VELOCITY_MAX, 20),
            // TODO Figure out if high acceleration > high PID?
            // TODO Turning PID only works if slot < 2. Thought there were 4??? Is it aux only?
            new Gains("Turn", 0, 4.0, 0.004, 18.0, 1023.0 / config.VELOCITY_MAX, 35, (int) (config.VELOCITY_MAX * 0.95), (int) (config.VELOCITY_MAX * 0.95)),
            //            new Gains("Turn", 0, 1.0, 0.001, 9.0, 1023.0 / config.VELOCITY_MAX, 35, (int) (config.VELOCITY_MAX
        });

        // config.GEAR_RATIO_HIGH = (36.0 / 15.0) * (24.0 / 40.0);
        config.GEAR_RATIO_LOW = (36.0 / 15.0) * (40.0 / 24.0);
        config.SCRUB = 0.96;
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

    public static RobotConfig getInstance()
    {
        if (instance == null)
        {
            instance = (RobotConfig) RobotConfigPicker.get(new EnhancedRobotConfig[] {
                new RobotConfig(),
            });
        }
        return instance;
    }
}

