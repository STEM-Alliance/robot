package org.wfrobotics.reuse.utilities;

import org.wfrobotics.Utilities;

import edu.wpi.first.wpilibj.Timer;

/**
 * A basic PID controller for use with the Swerve drive
 * @author Team 4818 WFRobotics
 */
public final class PIDController 
{
    private static final double MaxTimestampDiff = 1.0; // Heuristic to clear out the integral after being disabled.

    private double m_P;
    private double m_I;
    private double m_D;
    private double m_MaxOutput;
    private double m_MinOutput;
    private boolean preStageClamp = true;

    private double integral;
    private double prevTime;
    private double lastError;
    private double totalError;

    
    /**
     * Create a new instance of the PIController
     * @param p proportional component
     * @param i integral component, 0 to disable
     * @param d derivative component, 0 to disable
     * @param maxOutput maximum magnitude of the controller (typically 1.0, always positive)
     */
    public PIDController(double p, double i, double d, double maxOutput)
    {
        this(p, i, d, maxOutput, 0);
    }
    
    /**
     * Create a new instance of the PIController
     * @param p proportional component
     * @param i integral component, 0 to disable
     * @param d derivative component, 0 to disable
     * @param maxOutput maximum magnitude of the controller (typically 1.0, always positive)
     * @param minOutput minimum magnitude of the controller, used to get over system friction (0-maxOutput)
     */
    public PIDController(double p, double i, double d, double maxOutput, double minOutput)
    {
        this.m_P = p;
        this.m_I = i;
        this.m_D = d;
        this.m_MaxOutput = maxOutput;
        this.m_MinOutput = minOutput;

        this.integral = 0;
        this.prevTime = 0.0f;
    }
    
    /**
     * Update the PIController using the setpoint and the sensor reading
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
     * @param error new error to use for calculations
     * @return new calculated output, clamped to max output range
     */
    public double update(double error)
    {
        // Get time since we were last called.
        double currTime = Timer.getFPGATimestamp();
        double timeDiff = currTime - prevTime;
        prevTime = currTime;
        
        // Proportional term.
        double proportional = error * m_P;
        if(preStageClamp) proportional = Utilities.clampToRange(proportional, m_MaxOutput);
        
        double derivative = 0;
        if(timeDiff > 0)
        {
            derivative = (error - lastError) / timeDiff * m_D;
            if(preStageClamp) derivative = Utilities.clampToRange(derivative, m_MaxOutput);
        }

        totalError += error;
        
        if (timeDiff <= MaxTimestampDiff)
        {
            // Integrate the proportional term over time.
            integral += totalError * timeDiff * m_I;
            if(preStageClamp) integral = Utilities.clampToRange(integral, m_MaxOutput);
        }
        else
        {
            // We were probably disabled; reset the integral.
            integral = 0;
            derivative = 0;
        }
        
        this.lastError = error;

        // Calculate output with coefficients.
        double clampedVal = proportional + integral + derivative;
        clampedVal = Utilities.clampToRange(clampedVal, m_MaxOutput);
        
        // Make sure it's more than the specified minimum
        if(Math.abs(clampedVal) > .01)
        {
            if(Math.abs(clampedVal) < m_MinOutput)
            {
                clampedVal = Math.signum(clampedVal) * m_MinOutput;
            }
        }
        else
        {
            clampedVal = 0;
        }
        
        return clampedVal;
    }

    public double getP()
    {
        return m_P;
    }
    
    public double getI()
    {
        return m_I;
    }
    
    public double getD()
    {
        return m_D;
    }
    
    public void setP(double P)
    {
        this.m_P = P;
    }

    public void setI(double I)
    {
        this.m_I = I;
    }
    
    public void setD(double D)
    {
        this.m_D = D;
    }

    public void setMax(double max)
    {
        this.m_MaxOutput = max;
    }
    
    public void setMin(double min)
    {
        this.m_MinOutput = min;
    }
    
    public void resetError()
    {
        this.integral = 0;
        this.lastError = 0;
        this.totalError = 0;
    }
    
    /**
     * Enable or disable clamping during calculation stages, rather than just the output
     * @param preStageClamp if true, clamp at each calculation step (P, I, D)
     */
    public void preStageClamp(boolean preStageClamp)
    {
        this.preStageClamp  = preStageClamp;
    }
}
