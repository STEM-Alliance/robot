package com.taurus;

public final class PIController
{
    // Heuristic to clear out the integral after being disabled.
    private static final double MaxTimestampDiff = 1.0; 

    // Configuration.
    private final double P, I, maxOutput;
    
    // State.
    public double integral;
    public double lastTimestamp;

    public PIController(double p, double i, double maxOutput)
    {
        this.P = p;
        this.I = i;
        this.maxOutput = maxOutput;
        
        this.integral = 0;
        this.lastTimestamp = Double.NEGATIVE_INFINITY;
    }

    public double update(double error, double timestamp)
    {
        // Proportional term.
        double proportional = Utilities.clampToRange(
                error * P,
                -this.maxOutput, 
                this.maxOutput);
        
        // Get time since we were last called.
        double timeDiff = timestamp - this.lastTimestamp;
        this.lastTimestamp = timestamp;
        
        if (timeDiff < MaxTimestampDiff)
        {
            // Integrate the proportional term over time.
            this.integral += proportional * timeDiff * I;
            this.integral = Utilities.clampToRange(this.integral, -this.maxOutput, this.maxOutput);
        }
        else
        {
            // We were probably disabled; reset the integral.
            this.integral = 0;
        }
        
        // Calculate output with coefficients.
        return Utilities.clampToRange(
                proportional + this.integral, 
                -this.maxOutput, 
                this.maxOutput);
    }
}
