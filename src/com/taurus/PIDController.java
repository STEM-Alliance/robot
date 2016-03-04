package com.taurus;

import edu.wpi.first.wpilibj.Timer;

/**
 * A basic PID controller for use with the Swerve drive
 * 
 * @author Team 4818 Taurus Robotics
 *
 */
public final class PIDController {
    // Heuristic to clear out the integral after being disabled.
    private static final double MaxTimestampDiff = 1.0;

    // Configuration.
    private double P;
    private double I;
    private double D;

    private double  maxOutput;

    // State.
    public double integral;
    public double lastTimestamp;
    public double lastError;

    /**
     * Create a new instance of the PIController
     * 
     * @param p proportional component
     * @param i integral component, 0 to disable
     * @param d derivative component, 0 to disable
     * @param maxOutput maximum output of the controller (typically 1.0)
     */
    public PIDController(double p, double i, double d, double maxOutput)
    {
        this.P = p;
        this.I = i;
        this.D = d;
        this.maxOutput = maxOutput;

        this.integral = 0;
        this.lastTimestamp = Double.NEGATIVE_INFINITY;
    }
    
    /**
     * Update the PIController using the setpoint and the sensor reading
     * 
     * @param setpoint desired setpoint
     * @param sensor reading from the sensor
     * @param timestamp current time
     * @return new calculated output, clamped to max output range
     */
    public double update(double setpoint, double sensor)
    {
        return this.update(setpoint - sensor);
    }
    
    /**
     * Update the PIController using the new error
     * 
     * @param error new error to use for calculations
     * @return new calculated output, clamped to max output range
     */
    public double update(double error)
    {
        double timestamp = Timer.getFPGATimestamp();
        
        // Proportional term.
        double proportional = Utilities.clampToRange(error * P,
                -this.maxOutput, this.maxOutput);

        // Get time since we were last called.
        double timeDiff = timestamp - this.lastTimestamp;
        this.lastTimestamp = timestamp;
        
        double derivative = Utilities.clampToRange((error - lastError) / timeDiff * D, 
                -this.maxOutput, this.maxOutput);

        if (timeDiff < MaxTimestampDiff)
        {
            // Integrate the proportional term over time.
            this.integral += proportional * timeDiff * I;
            this.integral = Utilities.clampToRange(this.integral,
                    -this.maxOutput, this.maxOutput);
        }
        else
        {
            // We were probably disabled; reset the integral.
            this.integral = 0;
            derivative = 0;
        }
        
        this.lastError = error;

        // Calculate output with coefficients.
        return Utilities.clampToRange(proportional + this.integral + derivative,
                -this.maxOutput, this.maxOutput);
    }

    public double getP()
    {
        return P;
    }
    
    public double getI()
    {
        return I;
    }
    
    public double getD()
    {
        return D;
    }
    
    public void setP(double P)
    {
        this.P = P;
    }

    public void setI(double I)
    {
        this.I = I;
    }
    
    public void setD(double D)
    {
        this.D = D;
    }

    public void setMax(double max)
    {
        this.maxOutput = max;
        
    }
}
