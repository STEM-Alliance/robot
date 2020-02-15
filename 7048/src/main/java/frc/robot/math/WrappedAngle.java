package frc.robot.math;

/** Wrapped angles support their own range in degrees, agreeing to "wrap" all values back into this range automatically */
public interface WrappedAngle
{
    public double getAngle();                           // Degrees
    public WrappedAngle rotate(double angle);           // Positive: Rotate clockwise by angle     Negative: Rotate counterclockwise by angle
    public WrappedAngle rotate(WrappedAngle b);         // Positive: Rotate clockwise by b         Negative: Rotate counterclockwise by b
    public WrappedAngle rotateReverse(WrappedAngle b);  // Positive: Rotate counterclockwise by b  Negative: Rotate clockwise by b
}
