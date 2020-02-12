package frc.robot.config;

import frc.robot.config.TalonConfig.ClosedLoopConfig;

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

    @FunctionalInterface
    public interface TankConfigSupplier
    {
        TankConfig getTankConfig();
    }
}
