package org.wfrobotics.reuse.subsystems.motors;

public class ConfigPID {
    // Required
    public double p;
    
    // Optional
    public double i;
    public double d;
    public double f;
    public int iZone;
    public double rampRate;
    public double maxOutput;

    public ConfigPID(ConfigPIDBuilder builder)
    {
        this.p = builder.p;
        
        this.i = builder.i;
        this.d = builder.d;
        this.f = builder.f;
        
        this.iZone = builder.iZone;
        this.rampRate = builder.rampRate;
        
        this.maxOutput = builder.maxOutput;
    }
    
    public static class ConfigPIDBuilder {
        // Required
        private final double p;
        
        // Optional, so give default values
        private double i = 0;
        private double d = 0;
        private double f = 0;
        private int iZone = 0;
        private double rampRate = 0;
        private double maxOutput = 1;
        
        public ConfigPIDBuilder(double p)
        {
            this.p = p;
        }

        public ConfigPIDBuilder i(double i) { this.i = i; return this; }
        public ConfigPIDBuilder d(double d) { this.d = d; return this; }
        public ConfigPIDBuilder f(double f) { this.f = f; return this; }
        public ConfigPIDBuilder iZone(int iZone) { this.iZone = iZone; return this; }
        public ConfigPIDBuilder ramp(double ramp) { this.rampRate = ramp; return this; }
        public ConfigPIDBuilder maxOutput(double maxOutput) { this.maxOutput = maxOutput; return this; }
        
        public ConfigPID build() { return new ConfigPID(this); }
    }
}
