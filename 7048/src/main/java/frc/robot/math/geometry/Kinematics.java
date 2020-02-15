package frc.robot.math.geometry;

import frc.robot.config.TankConfig;
import frc.robot.EnhancedRobot;

/**
 * Kinematics: Estimate the robot's motion based on sensor values
 *
 * <p><b>forwardKinematics:</b> Estimate the robot's movement forwards based on observed sensor value changes
 * <p><b>integrateForwardKinematics:</b> Estimate the robot's new position based on observed sensor value changes & prior position
 * <p><b>inverseKinematics:</b> Estimate the robot's velocity required to move forwards an amount
 *
 * <p>Provides forward and inverse kinematics equations for the robot modeling the wheelbase as a differential drive (with
 * a corrective factor to account for skidding).
 * @author Code: Team 254<br>Documentation: STEM Alliance of Fargo Moorhead
 */
public final class Kinematics {
    private static final double kEpsilon = 1E-9;
    private static final double kTrackScrubFactor;
    private static final double kTrackWidthInches;

    static
    {
        final TankConfig config = EnhancedRobot.getConfig().getTankConfig();
        kTrackScrubFactor = config.SCRUB;
        kTrackWidthInches = config.WIDTH;
    }

    private Kinematics()
    {
        // No instantiation
    }

    /**
     * Forward kinematics using only encoders, rotation is implicit (less accurate than below, but useful for predicting
     * motion)
     */
    public static Twist2d forwardKinematics(double left_wheel_delta, double right_wheel_delta) {
        double delta_v = (right_wheel_delta - left_wheel_delta) / 2 * kTrackScrubFactor;
        double delta_rotation = delta_v * 2 / kTrackWidthInches;
        return forwardKinematics(left_wheel_delta, right_wheel_delta, delta_rotation);
    }

    /**
     * Forward kinematics using encoders and explicitly measured rotation (ex. from gyro)
     */
    public static Twist2d forwardKinematics(double left_wheel_delta, double right_wheel_delta,
                                            double delta_rotation_rads) {
        final double dx = (left_wheel_delta + right_wheel_delta) / 2.0;
        return new Twist2d(dx, 0, delta_rotation_rads);
    }

    /**
     * For convenience, forward kinematic with an absolute rotation and previous rotation.
     */
    public static Twist2d forwardKinematics(Rotation2d prev_heading, double left_wheel_delta, double right_wheel_delta,
                                            Rotation2d current_heading) {
        return forwardKinematics(left_wheel_delta, right_wheel_delta,
                                        prev_heading.inverse().rotateBy(current_heading).getRadians());
    }

    /** Append the result of forward kinematics to a previous pose. */
    public static Pose2d integrateForwardKinematics(Pose2d current_pose, double left_wheel_delta,
                                                              double right_wheel_delta, Rotation2d current_heading) {
        Twist2d with_gyro = forwardKinematics(current_pose.getRotation(), left_wheel_delta, right_wheel_delta,
                                        current_heading);
        return integrateForwardKinematics(current_pose, with_gyro);
    }

    /**
     * For convenience, integrate forward kinematics with a Twist2d and previous rotation.
     */
    public static Pose2d integrateForwardKinematics(Pose2d current_pose,
                                                              Twist2d forward_kinematics) {
        return current_pose.transformBy(Pose2d.exp(forward_kinematics));
    }

    /**
     * Class that contains left and right wheel velocities
     */
    public static class DriveVelocity {
        public final double left;
        public final double right;

        public DriveVelocity(double left, double right) {
            this.left = left;
            this.right = right;
        }
    }

    /**
     * Uses inverse kinematics to convert a Twist2d into left and right wheel velocities
     */
    public static DriveVelocity inverseKinematics(Twist2d velocity) {
        if (Math.abs(velocity.dtheta) < kEpsilon) {
            return new DriveVelocity(velocity.dx, velocity.dx);
        }
        double delta_v = kTrackWidthInches * velocity.dtheta / (2 * kTrackScrubFactor);
        return new DriveVelocity(velocity.dx - delta_v, velocity.dx + delta_v);
    }
}
