package org.wfrobotics.reuse.utilities;

/** Wrapped angles support -180 to 180 degrees, agreeing to "wrap" all other values back into this range automatically */
public interface WrappedAngle
{
    public double getAngle();                           // Degrees
    public WrappedAngle rotate(double angle);           // Positive: Rotate clockwise by angle     Negative: Rotate counterclockwise by angle
    public WrappedAngle rotate(WrappedAngle b);         // Positive: Rotate clockwise by b         Negative: Rotate counterclockwise by b
    public WrappedAngle rotateReverse(WrappedAngle b);  // Positive: Rotate counterclockwise by b  Negative: Rotate clockwise by b
}
