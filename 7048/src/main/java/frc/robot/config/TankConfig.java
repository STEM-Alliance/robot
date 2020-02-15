package frc.robot.config;

import frc.robot.config.TalonConfig.ClosedLoopConfig;
import frc.robot.hardware.Gyro;
import frc.robot.hardware.GyroNavx;
import frc.robot.subsystems.Lookahead;
import frc.robot.subsystems.PathFollower;
import frc.robot.subsystems.TankMaths;
import frc.robot.subsystems.PathFollower.Parameters;

/** @author STEM Alliance of Fargo Moorhead */
public class TankConfig
{
    public boolean TUNING = false;
    public boolean CLOSED_LOOP_ENABLED = true;

    /** Native */
    public double VELOCITY_MAX;
    /** Native */
    public int VELOCITY_PATH;
    /** Native */
    public int ACCELERATION;
    /** DriveDistance heading PID */
    public double STEERING_DRIVE_DISTANCE_P;
    /** DriveDistance heading PID */
    public double STEERING_DRIVE_DISTANCE_I;
    /** Enable brake mode in teleop? */
    public boolean OPEN_LOOP_BRAKE = false;
    /** What percent output and below will talon treat as zero. Makes sure you don't set joysticks when you don't mean to */
    public double OPEN_LOOP_DEADBAND = 0.04;
    /** How fast tank will accelerate both when both applying throttle and turning. Trades responsiveness for sensitivity */
    public double OPEN_LOOP_RAMP = 0.05;
    /** Limit the percent output to this percentage. Use if your top speed is uncontrollable */
    public double MAX_PERCENT_OUT = 1.0;

    /** Settings to configure the talon and any followers for use with the encoder */
    public ClosedLoopConfig CLOSED_LOOP;

    // --------------------------------------
    // -------------- Geometry --------------
    // --------------------------------------

    /** Total reduction from encoder to wheel in low gear */
    public double GEAR_RATIO_LOW;
    /** Turning correction for perpendicular "cross track" friction to wheels */
    public double SCRUB = 0.96;
    /** Inches */
    public double WHEEL_DIAMETER;
    /** Distance across the drivetrain. Measure from center of wheel to center of wheel. */
    public double WIDTH;

    /** Path velocity controller and steering controller config. Subclass to use non-standard tuning */
    public Parameters getPathConfig()
    {
        final double maxInchesPerSecond = TankMaths.kVelocityMaxInchesPerSecond;  // 120.0
        final double minLookahead = 12.0;  // Inches, 12.0
        final double maxLookahead = 24.0;  // Inches, 24.0
        final double minLookaheadSpeed = 9.0 / 120.0 * maxInchesPerSecond; // Inches/s, 9.0
        final double maxLookaheadSpeed = maxInchesPerSecond; //Inches/s, 120.0
        final double kInertiaSteering = 0.0;  // 0.0
        final double kP = 5.0 * maxInchesPerSecond / 120.0; // 5.0, Did our 10fps / their 18fps * their original on these three
        final double kI = 0.03 * maxInchesPerSecond / 120.0;  // 0.03
        final double kV = 0.02 * maxInchesPerSecond / 120.0;  // 0.02 TODO Investigate units, did we change in correct direction?
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

    public Gyro getGyroHardware()
    {
        return new GyroNavx();
    }

    @FunctionalInterface
    public interface TankConfigSupplier
    {
        TankConfig getTankConfig();
    }
}
