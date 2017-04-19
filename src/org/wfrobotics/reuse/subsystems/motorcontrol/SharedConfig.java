package org.wfrobotics.reuse.subsystems.motorcontrol;

public abstract class SharedConfig
{
    public final int address;
    public double tolerance;
    
    public SharedConfig(int address)
    {
        this.address = address;
    }
    
    public abstract Motor makeMotor();
}