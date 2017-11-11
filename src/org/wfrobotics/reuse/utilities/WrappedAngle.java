package org.wfrobotics.reuse.utilities;

public interface WrappedAngle
{
    public double getAngle();
    public WrappedAngle rotate(WrappedAngle b);
    public WrappedAngle rotate(double angle);
}
