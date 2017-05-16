package org.wfrobotics.reuse.hardware.interfaces;

public interface Motor 
{
    public void set(double desired);
    public void setBrake(boolean enable);
}
