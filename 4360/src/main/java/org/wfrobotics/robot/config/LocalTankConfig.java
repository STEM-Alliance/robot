package org.wfrobotics.robot.config;

import org.wfrobotics.reuse.commands.drive.DriveCheesy;
import org.wfrobotics.reuse.config.TalonConfig.ClosedLoopConfig;
import org.wfrobotics.reuse.config.TankConfig;
import org.wfrobotics.reuse.hardware.sensors.Gyro;
import org.wfrobotics.reuse.hardware.sensors.GyroNavx;
import org.wfrobotics.reuse.subsystems.drive.Lookahead;
import org.wfrobotics.reuse.subsystems.drive.PathFollower;
import org.wfrobotics.reuse.subsystems.drive.PathFollower.Parameters;
import org.wfrobotics.reuse.subsystems.drive.TankMaths;
import org.wfrobotics.robot.commands.DriveTeleop;
import edu.wpi.first.wpilibj.command.Command;

/** @author STEM Alliance of Fargo Moorhead */
public class LocalTankConfig extends TankConfig
{
    /*
    public boolean TUNING = false;
    public double VELOCITY_MAX;
    public int VELOCITY_PATH;
    public int ACCELERATION;
    public double STEERING_DRIVE_DISTANCE_P;
    public double STEERING_DRIVE_DISTANCE_I;
    public boolean OPEN_LOOP_BRAKE = false;
    public double OPEN_LOOP_DEADBAND = 0.04;
    public double OPEN_LOOP_RAMP = 0.05;
    public ClosedLoopConfig CLOSED_LOOP;

    // --------------------------------------
    // -------------- Geometry --------------
    // --------------------------------------

    // Total reduction from encoder to wheel in high gear
    public double GEAR_RATIO_HIGH;
    // Total reduction from encoder to wheel in low gear 
    public double GEAR_RATIO_LOW;
    // Turning correction for perpendicular "cross track" friction to wheels
    public double SCRUB;
    public double WHEEL_DIAMETER;
    // Distance across the drivetrain side to side
    public double WIDTH;

    public Parameters getPathConfig()
    {
        final double maxInchesPerSecond = TankMaths.kVelocityMaxInchesPerSecond;  // 120.0
        final double minLookahead = 12.0;  // Inches, 12.0
        final double maxLookahead = 24.0;  // Inches, 24.0
        final double minLookaheadSpeed = 9.0 / 120.0 * maxInchesPerSecond; // Inches/s, 9.0
        final double maxLookaheadSpeed = maxInchesPerSecond;  //Inches/s, 120.0
        final double kInertiaSteering = 0.0;  // 0.0
        final double kP = 3.0;      // 5.0, Did our 10fps / their 18fps * their original on these three
        final double kI = 0.18;     // 0.03
        final double kV = 0.12;     // 0.02 TODO Investigate units, did we change in correct direction?
        final double kFeedForwardVelocity = 1.0;            // 1.0
        final double kFeedForwardAcceleration = 0.25;       // 0.05 TODO Investigate units, compare to characterization
        final double kCruiseVelocity = maxInchesPerSecond;  // Inches/s, 120.0
        final double kAcceleration = kCruiseVelocity;       // Inches/s^2, 120.0
        final double kGoalTolPosition = 0.75;        // Inches within goal to finish MP, 0.75
        final double kGoalTolVelocity = 12.0;        // Inches/s within goal to finish MP, 12.0
        final double kStopSteeringDistance = 9.0;    // Inches
        final Lookahead lookahead = new Lookahead(minLookahead, maxLookahead, minLookaheadSpeed, maxLookaheadSpeed);
        return new PathFollower.Parameters(lookahead, kInertiaSteering, kP, kI, kV, kFeedForwardVelocity, kFeedForwardAcceleration, kCruiseVelocity, kAcceleration, kGoalTolPosition, kGoalTolVelocity, kStopSteeringDistance);
    }

    public Gyro getGyroHardware()
    {
        return new GyroNavx();  // Subclass to use another type
    }

    @FunctionalInterface
    public interface TankConfigSupplier
    {
        TankConfig getTankConfig();
    }
    */
    
	@Override
    public Command getTeleopCommand()
    {
        return new DriveTeleop();  // Subclass to use another command
    }
}