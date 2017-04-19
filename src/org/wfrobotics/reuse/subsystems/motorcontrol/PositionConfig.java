package org.wfrobotics.reuse.subsystems.motorcontrol;

public abstract class PositionConfig extends SharedConfig
{
    public double[] positions;
    
    public PositionConfig(int address)
    {
        super(address);
    }
}
