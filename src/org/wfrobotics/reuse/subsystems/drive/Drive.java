package org.wfrobotics.reuse.subsystems.drive;

import org.wfrobotics.reuse.utilities.HerdVector;

public interface Drive
{
    public void driveBasic(HerdVector vector);
    public void turnBasic(HerdVector vector);
    public void setBrake(boolean enable);
    public void setGear(boolean useHighGear);
    public void zeroGyro();
}
