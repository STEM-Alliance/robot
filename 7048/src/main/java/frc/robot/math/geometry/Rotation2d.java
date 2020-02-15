package frc.robot.math.geometry;

import static frc.robot.math.geometry.Util.kEpsilon;

import java.text.DecimalFormat;

/**
 * Rotation2d is a point <b>on</b> the unit circle, making its magnitude always ~1.
 * It focuses on describing angles.
 * <pre>
 *              (0, 1) = 90
 *                -
 *                -              (cos(), sin())
 *                -           -
 *                -        -
 *                -     -
 *                -  -
 * ------------------------------- (1, 0) = 0
 *                -  -
 *                -     -
 *                -        -
 *                -           -
 *                -              (cos(), -sin()) = inverse(cos(), sin())
 *              (0, 1) = -90
 * </pre>
 *<p><a href="https://mathbitsnotebook.com/Geometry/Transformations/TRRigidTransformations.html">See more</a>
 *
 * <p><b>fromDegrees:</b> Easy way to create a Rotation2d
 * <p><b>getDegrees:</b> Easy way to use Rotation2d results
 * <p><b>Interpolate:</b> Calculate a point a <b>percentage between two points</b> (ex: (0, 90, .5) = 45, (45, null, .5) = 45, (null, 90, .5) = 90)
 * <p><b>Inverse:</b> Flip the point <b>about the X axis</b> (ex: (45) = -45, (0, 1) = (0, -1)) Note: cos remains the same, sin's sign will switch
 * <p><b>Parallel:</b> Are the points <b>within an epsilon</b> of the same angle? (ex: (45, 45) = true, (45, -135) = True, (45, -45) = False)
 * <p><b>Rotate:</b> <b>Sum</b> the angles (ex: 45 + 45 = 90, 45 + -45 = 0)
 *
 * <p>A rotation in a 2d coordinate frame represented a point on the unit circle (cosine and sine).
 *
 * Inspired by Sophus (https://github.com/strasdat/Sophus/tree/master/sophus)
 * @author Code: Team 254<br>Documentation: STEM Alliance of Fargo Moorhead
 */
public class Rotation2d implements IRotation2d<Rotation2d>  {
    protected static final Rotation2d kIdentity = new Rotation2d();

    public static final Rotation2d identity() {
        return kIdentity;
    }

    protected final double cos_angle_;
    protected final double sin_angle_;

    /** Identity Rotation2d, on X axis (1, 0) */
    public Rotation2d() {
        this(1, 0, false);
    }

    public Rotation2d(double x, double y, boolean normalize) {
        if (normalize) {
            // From trig, we know that sin^2 + cos^2 == 1, but as we do math on this object we might accumulate rounding errors.
            // Normalizing forces us to re-scale the sin and cos to reset rounding errors.
            double magnitude = Math.hypot(x, y);
            if (magnitude > kEpsilon) {
                sin_angle_ = y / magnitude;
                cos_angle_ = x / magnitude;
            } else {
                sin_angle_ = 0;
                cos_angle_ = 1;
            }
        } else {
            cos_angle_ = x;
            sin_angle_ = y;
        }
    }

    /** Clone a Rotation2d so changes wont impact the original object */
    public Rotation2d(final Rotation2d other) {
        cos_angle_ = other.cos_angle_;
        sin_angle_ = other.sin_angle_;
    }

    public Rotation2d(final Translation2d direction, boolean normalize) {
        this(direction.x(), direction.y(), normalize);
    }

    public static Rotation2d fromRadians(double angle_radians) {
        return new Rotation2d(Math.cos(angle_radians), Math.sin(angle_radians), false);
    }

    public static Rotation2d fromDegrees(double angle_degrees) {
        return fromRadians(Math.toRadians(angle_degrees));
    }

    /** The X component of the point on the unit circle */
    public double cos() {
        return cos_angle_;
    }

    /** The Y component of the point on the unit circle */
    public double sin() {
        return sin_angle_;
    }

    public double tan() {
        if (Math.abs(cos_angle_) < kEpsilon) {
            if (sin_angle_ >= 0.0) {
                return Double.POSITIVE_INFINITY;
            }
            return Double.NEGATIVE_INFINITY;
        }
        return sin_angle_ / cos_angle_;
    }

    public double getRadians() {
        return Math.atan2(sin_angle_, cos_angle_);
    }

    public double getDegrees() {
        return Math.toDegrees(getRadians());
    }

    /**
     * We can rotate this Rotation2d by adding together the effects of it and another rotation.
     *
     * @param other The other rotation. See: https://en.wikipedia.org/wiki/Rotation_matrix
     * @return This rotation rotated by other.
     */
    public Rotation2d rotateBy(final Rotation2d other) {
        return new Rotation2d(cos_angle_ * other.cos_angle_ - sin_angle_ * other.sin_angle_,
                                        cos_angle_ * other.sin_angle_ + sin_angle_ * other.cos_angle_, true);
    }

    public Rotation2d normal() {
        return new Rotation2d(-sin_angle_, cos_angle_, false);
    }

    /**
     * The inverse of a Rotation2d "undoes" the effect of this rotation.
     *
     * @return The opposite of this rotation.
     */
    public Rotation2d inverse() {
        return new Rotation2d(cos_angle_, -sin_angle_, false);
    }

    /** Are the points <b>within an epsilon</b> of the same angle? (ex: (45, 45) = true, (45, -135) = True, (45, -45) = False) */
    public boolean isParallel(final Rotation2d other) {
        return Util.epsilonEquals(Translation2d.cross(toTranslation(), other.toTranslation()), 0.0);
    }

    public Translation2d toTranslation() {
        return new Translation2d(cos_angle_, sin_angle_);
    }

    /**Calculate a point a <b>percentage between two points</b> (ex: (0, 90, .5) = 45, (45, null, .5) = 45, (null, 90, .5) = 90) */
    @Override
    public Rotation2d interpolate(final Rotation2d other, double x) {
        if (x <= 0) {
            return new Rotation2d(this);
        } else if (x >= 1) {
            return new Rotation2d(other);
        }
        double angle_diff = inverse().rotateBy(other).getRadians();
        return rotateBy(Rotation2d.fromRadians(angle_diff * x));
    }

    @Override
    public String toString() {
        final DecimalFormat fmt = new DecimalFormat("#0.000");
        return "(" + fmt.format(getDegrees()) + " deg)";
    }

    @Override
    public String toCSV() {
        final DecimalFormat fmt = new DecimalFormat("#0.000");
        return fmt.format(getDegrees());
    }

    @Override
    public double distance(final Rotation2d other) {
        return inverse().rotateBy(other).getRadians();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof Rotation2d)) return false;
        return distance((Rotation2d) other) < Util.kEpsilon;
    }

    @Override
    public Rotation2d getRotation() {
        return this;
    }
}
