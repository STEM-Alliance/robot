package org.wfrobotics.reuse.subsystems.swerve;

public interface WheelAngleMotor 
{
    public void set(double speed);
    public double getAnglePotAdjusted();
    public void setPotOffset(double offset);
    public double debugGetPotRaw();
    public void free();
}
