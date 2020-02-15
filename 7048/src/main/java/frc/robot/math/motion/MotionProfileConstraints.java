package frc.robot.math.motion;

/**
 * Config for building a {@link MotionProfile} path.
 *
 * <p>Constraints for constructing a MotionProfile.
 * @author Code: Team 254<br>Documentation: STEM Alliance of Fargo Moorhead
 */
public class MotionProfileConstraints {
    protected double max_abs_vel = Double.POSITIVE_INFINITY;
    protected double max_abs_acc = Double.POSITIVE_INFINITY;

    public MotionProfileConstraints(double max_vel, double max_acc) {
        max_abs_vel = Math.abs(max_vel);
        max_abs_acc = Math.abs(max_acc);
    }

    /**
     * @return The (positive) maximum allowed velocity.
     */
    public double max_abs_vel() {
        return max_abs_vel;
    }

    /**
     * @return The (positive) maximum allowed acceleration.
     */
    public double max_abs_acc() {
        return max_abs_acc;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MotionProfileConstraints)) {
            return false;
        }
        final MotionProfileConstraints other = (MotionProfileConstraints) obj;
        return (other.max_abs_acc() == max_abs_acc()) && (other.max_abs_vel() == max_abs_vel());
    }
}
