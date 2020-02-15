package frc.robot.math.geometry;

import java.text.DecimalFormat;

/**
 * Twist2d represents a <b>change in robot location at CONSTANT velocity, along a circle</b>, over a <i>duration</i> of time.
 * * <pre>
 *              (0, 1)
 *                            |^|
 *                -           |*|
 *                -           .
 *                           .   <-- Imagine that's constant curvature ¯\_(ツ)_/¯
 *                - -      .
 * ----------     * >   .     -------
 *                - -
 *
 *                -
 *                -
 *                -
 *              (0, 1)
 * </pre>
 *
 * <p><i>"Our robot moves in a 2D plane with two translational dimensions (x and y) and one rotational dimension (yaw). Sometimes we want to know where we will end up given an instantaneous parametric velocity (dx/ds, dy/ds, dtheta/ds) and a value for the parameter (ex. time duration if the parameter 's' represents time). A simple way to do this is to assume that in a given period 'ds' the robot moves in a straight line and then turns (or visa versa)...to obtain this, just multiply each component by 'ds'. If 'ds' and curvature of the motion is small, this is a pretty good approximation. However, if 'ds' and curvature are large, this can introduce error that gets compounded over time, since in reality never move in a straight line, since we are simultaneously translating and rotating.
 * <p>Luckily, there is a precise formula for obtaining a new pose assuming constant curvature displacement which we can borrow from the mathematical field of differential geometry (often used in PhD-level robot kinematics and computer vision). The term "twist" is borrowed from this field because constant-curvature displacement can be thought of as representing the twisting of a screw (especially if you think about the 3D case where there is also a "pitch" velocity). The "exp" and "log" functions likewise refer to the group exponential and group logarithm functions that are well defined in this field (for obtaining a new pose from a twist, and obtaining a twist from a pose, respectively)."</i> - Jared Russell
 * <p><a href="https://www.chiefdelphi.com/forums/showthread.php?p=1698487">Thread from Chief Delphi</a>
 *
 * <p>A movement along an arc at constant curvature and velocity. We can use ideas from "differential calculus" to create
 * new RigidTransform2d's from a Twist2d and visa versa.
 *
 * A Twist can be used to represent a difference between two poses, a velocity, an acceleration, etc.
 * @author Code: Team 254<br>Documentation: STEM Alliance of Fargo Moorhead
 */
public class Twist2d {
    protected static final Twist2d kIdentity = new Twist2d(0.0, 0.0, 0.0);

    public static final Twist2d identity() {
        return kIdentity;
    }

    public final double dx;
    public final double dy;
    public final double dtheta; // Radians!

    public Twist2d(double dx, double dy, double dtheta) {
        this.dx = dx;
        this.dy = dy;
        this.dtheta = dtheta;
    }

    public Twist2d scaled(double scale) {
        return new Twist2d(dx * scale, dy * scale, dtheta * scale);
    }

    public double norm() {
        // Common case of dy == 0
        if (dy == 0.0)
            return Math.abs(dx);
        return Math.hypot(dx, dy);
    }

    public double curvature() {
        if (Math.abs(dtheta) < Util.kEpsilon && norm() < Util.kEpsilon)
            return 0.0;
        return dtheta / norm();
    }

    @Override
    public String toString() {
        final DecimalFormat fmt = new DecimalFormat("#0.000");
        return "(" + fmt.format(dx) + "," + fmt.format(dy) + "," + fmt.format(Math.toDegrees(dtheta)) + " deg)";
    }
}