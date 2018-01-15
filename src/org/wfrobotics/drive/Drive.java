package org.wfrobotics.drive;

public interface Drive
{
    public void setBrake(boolean enable);
    public void setGear(boolean useHighGear);
    public void zeroGyro();
}
