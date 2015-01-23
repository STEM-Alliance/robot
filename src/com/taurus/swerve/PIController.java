package com.taurus.swerve;

import com.taurus.Utilities;

/**
 * A basic PI controller for use with the Swerve drive
 * @author Team 4818 Taurus Robotics
 *
 */
public final class PIController
{
    // Heuristic to clear out the integral after being disabled.
    private static final double MaxTimestampDiff = 1.0; 

    // Configuration.
    private final double P, I, maxOutput;
    
    // State.
    public double integral;
    public double lastTimestamp;

    /**
     * Create a new instance of the PIController
     * @param p proportional component
     * @param i integral component, 0 to disable
     * @param maxOutput maximum output of the controller (typically 1.0)
     */
    public PIController(double p, double i, double maxOutput)
    {
        this.P = p;
        this.I = i;
        this.maxOutput = maxOutput;
        
        this.integral = 0;
        this.lastTimestamp = Double.NEGATIVE_INFINITY;
    }

    /**
     * Update the PIController using the new error
     * @param error new error to use for calculations
     * @param timestamp current time
     * @return new calculated output, clamped to max output range 
     */
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
