package frc.robot.math.geometry;

import java.text.DecimalFormat;

/**
 * Translation2d is a vector (direction + magnitude) implemented in cartesian coordinates (x, y).
 * It focuses on describing location.
 * <pre>
 *              (0, 1)
 *                -
 *                -              (x, y) = inverse(-x, -y)
 *                -           -
 *                -        -
 *                -     -
 *                -  -
 * ------------------------------- (1, 0)
 *             -  -
 *          -     -
 *       -        -
 *    -           -
 * (-x, -y)       -
 *              (0, 1)
 * </pre>
 * <p><a href="https://mathbitsnotebook.com/Geometry/Transformations/TRRigidTransformations.html">See more</a>
 *
 * <p><b>Cross:</b> Cross product is the component of the first Translation2d that runs along the second Translation2d
 * <p><b>Direction:</b> Get as Rotation2D point on unit circle at this Translation2d's angle (ex: Translation2d(1, 1) = Rotation2d(PI/2, PI/2) = 45)
 * <p><b>Dot:</b> Dot product is a Translation2d that spans completes the triangle if you put the first two Translation2d's end to end
 * <p><b>Extrapolate:</b> Add the scaled difference of these Translation2d (ex: (2, 4), (3, 2), 2 = (4, 0))
 * <p><b>GetAngle:</b> The angle in <b>between</b> the two Translation2d
 * <p><b>Inverse:</b> <b>Flip signs</b> on x <b>and</b> y (ex: (1, 1) = (-1, -1)) Note: You can then subtract this vector with translateBy()
 * <p><b>Interpolate:</b> Add the scaled difference of these Translation2d, <b>bounded</b> by the two Translation2d
 * <p><b>Scale:</b> <b>Multiply</b> the magnitude by a number (ex: (3, 2) * 2 = (6, 4))
 * <p><b>TranslateBy:</b> <b>Sum</b> the vectors (ex: (2, 0) + (-2, 1) = (0, 1), (x, y) + inverse(x, y) = (0, 0))
 *
 * <p>A translation in a 2d coordinate frame. Translations are simply shifts in an (x, y) plane.
 * @author Code: Team 254<br>Documentation: STEM Alliance of Fargo Moorhead
 */
public class Translation2d implements ITranslation2d<Translation2d> {
    protected static final Translation2d kIdentity = new Translation2d();

    public static final Translation2d identity() {
        return kIdentity;
    }

    protected final double x_;
    protected final double y_;

    /** Identity Translation2d, on origin (0, 0) */
    public Translation2d() {
        x_ = 0;
        y_ = 0;
    }

    public Translation2d(double x, double y) {
        x_ = x;
        y_ = y;
    }

    /** Clone a Translate2d so changes wont impact the original object */
    public Translation2d(final Translation2d other) {
        x_ = other.x_;
        y_ = other.y_;
    }

    /** Subtract these two Translate2d to make the new one */
    public Translation2d(final Translation2d start, final Translation2d end) {
        x_ = end.x_ - start.x_;
        y_ = end.y_ - start.y_;
    }

    /**
     * The "norm" of a transform is the Euclidean distance in x and y.
     *
     * @return sqrt(x ^ 2 + y ^ 2)
     */
    public double norm() {
        return Math.hypot(x_, y_);
    }

    public double norm2() {
        return x_ * x_ + y_ * y_;
    }

    public double x() {
        return x_;
    }

    public double y() {
        return y_;
    }

    /**
     * We can compose Translation2d's by adding together the x and y shifts.
     *
     * @param other The other translation to add.
     * @return The combined effect of translating by this object and the other.
     */
    public Translation2d translateBy(final Translation2d other) {
        return new Translation2d(x_ + other.x_, y_ + other.y_);
    }

    /**
     * We can also rotate Translation2d's. See: https://en.wikipedia.org/wiki/Rotation_matrix
     *
     * @param rotation The rotation to apply.
     * @return This translation rotated by rotation.
     */
    public Translation2d rotateBy(final Rotation2d rotation) {
        return new Translation2d(x_ * rotation.cos() - y_ * rotation.sin(), x_ * rotation.sin() + y_ * rotation.cos());
    }

    /** Get as Rotation2D point on unit circle at this Translation2d's angle (ex: Translation2d(1, 1) = Rotation2d(PI/2, PI/2) = 45) */
    public Rotation2d direction() {
        return new Rotation2d(x_, y_, true);
    }

    /**
     * The inverse simply means a Translation2d that "undoes" this object.
     *
     * @return Translation by -x and -y.
     */
    public Translation2d inverse() {
        return new Translation2d(-x_, -y_);
    }

    @Override
    public Translation2d interpolate(final Translation2d other, double x) {
        if (x <= 0) {
            return new Translation2d(this);
        } else if (x >= 1) {
            return new Translation2d(other);
        }
        return extrapolate(other, x);
    }

    /** Add the scaled difference of these Translation2d (2, 4), (3, 2), 2 = (4, 0) */
    public Translation2d extrapolate(final Translation2d other, double x) {
        return new Translation2d(x * (other.x_ - x_) + x_, x * (other.y_ - y_) + y_);
    }

    /** <b>Multiply</b> the magnitude by a number (ex: (3, 2) * 2 = (6, 4)) */
    public Translation2d scale(double s) {
        return new Translation2d(x_ * s, y_ * s);
    }

    public boolean epsilonEquals(final Translation2d other, double epsilon) {
        return Util.epsilonEquals(x(), other.x(), epsilon) && Util.epsilonEquals(y(), other.y(), epsilon);
    }

    @Override
    public String toString() {
        final DecimalFormat fmt = new DecimalFormat("#0.000");
        return "(" + fmt.format(x_) + "," + fmt.format(y_) + ")";
    }

    @Override
    public String toCSV() {
        final DecimalFormat fmt = new DecimalFormat("#0.000");
        return fmt.format(x_) + "," + fmt.format(y_);
    }

    /** Dot product is a Translation2d that spans completes the triangle if you put the first two Translation2d's end to end */
    public static double dot(final Translation2d a, final Translation2d b) {
        return a.x_ * b.x_ + a.y_ * b.y_;
    }

    /** The angle in <b>between</b> the two Translation2d */
    public static Rotation2d getAngle(final Translation2d a, final Translation2d b) {
        double cos_angle = dot(a, b) / (a.norm() * b.norm());
        if (Double.isNaN(cos_angle)) {
            return new Rotation2d();
        }
        return Rotation2d.fromRadians(Math.acos(Math.min(1.0, Math.max(cos_angle, -1.0))));
    }

    /** Cross product is the component of the first Translation2d that runs along the second Translation2d */
    public static double cross(final Translation2d a, final Translation2d b) {
        return a.x_ * b.y_ - a.y_ * b.x_;
    }

    @Override
    public double distance(final Translation2d other) {
        return inverse().translateBy(other).norm();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null || !(other instanceof Translation2d)) return false;
        return distance((Translation2d) other) < Util.kEpsilon;
    }

    @Override
    public Translation2d getTranslation() {
        return this;
    }
}
