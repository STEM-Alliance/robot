package org.wfrobotics.reuse.hardware.motors.config;

public class PIDControllerConfig {
    // Required
    public final double p;
    
    // Optional, so give default values
    public double i = 0;
    public double d = 0;
    public double f = 0;
    public int iZone = 0;
    public double rampRate = 0;
    public double maxOutput = 1;
    
    public PIDControllerConfig(double p)
    {
        this.p = p;
    }

    public PIDControllerConfig i(double i) { this.i = i; return this; }
    public PIDControllerConfig d(double d) { this.d = d; return this; }
    public PIDControllerConfig f(double f) { this.f = f; return this; }
    public PIDControllerConfig iZone(int iZone) { this.iZone = iZone; return this; }
    public PIDControllerConfig ramp(double ramp) { this.rampRate = ramp; return this; }
    public PIDControllerConfig maxOutput(double maxOutput) { this.maxOutput = maxOutput; return this; }
    
    //public ConfigPID build() { return new ConfigPID(this); }
}
