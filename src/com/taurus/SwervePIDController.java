/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008-2012. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package com.taurus;

import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.util.BoundaryException;

/**
 * Class implements a PID Control Loop.
 *
 * Creates a separate thread which reads the given PIDSource and takes
 * care of the integral calculations, as well as writing the given
 * PIDOutput
 */
public class SwervePIDController {

    private double m_P;         // factor for "proportional" control
    private double m_I;         // factor for "integral" control
    private double m_D;         // factor for "derivative" control
    private double m_F;                 // factor for feedforward term
    private double m_maximumOutput = 1.0;   // |maximum output|
    private double m_minimumOutput = -1.0;  // |minimum output|
    private double m_maximumInput = 0.0;        // maximum input - limit setpoint to this
    private double m_minimumInput = 0.0;        // minimum input - limit setpoint to this
    private boolean m_continuous = false;   // do the endpoints wrap around? eg. Absolute encoder
    private boolean m_enabled = false;          //is the pid controller enabled
    private double m_prevError = 0.0;   // the prior sensor input (used to compute velocity)
    private double m_totalError = 0.0; //the sum of the errors for use in the integral calc
    private Tolerance m_tolerance;  //the tolerance object used to check if on target
    private double m_setpoint = 0.0;
    private double m_error = 0.0;
    private double m_result = 0.0;
    PIDSource m_pidInput;
    PIDOutput m_pidOutput;
    
    /**
     * Tolerance is the type of tolerance used to specify if the PID controller is on target.
     * The various implementations of this class such as PercentageTolerance and AbsoluteTolerance
     * specify types of tolerance specifications to use.
     */
    public interface Tolerance {
        public boolean onTarget();
    }
    
    public class PercentageTolerance implements Tolerance {
        double percentage;
        
        PercentageTolerance(double value) {
            percentage = value;
        }
    
        public boolean onTarget() {
            return (Math.abs(getError()) < percentage / 100
                    * (m_maximumInput - m_minimumInput));
        }
    }
    
    public class AbsoluteTolerance implements Tolerance {
        double value;
        
        AbsoluteTolerance(double value) {
            this.value = value;
        }
        
        public boolean onTarget() {
            return Math.abs(getError()) < value;
        }
    }
    
    public class NullTolerance implements Tolerance {
    
        public boolean onTarget() {
            throw new RuntimeException("No tolerance value set when using PIDController.onTarget()");
        }
    }

    /**
     * Return the current PID result
     * This is always centered on zero and constrained the the max and min outs
     * @return the latest calculated output
     */
    public double performPID() {
        calculate();
        return m_result;
    }

    /**
     * Allocate a PID object with the given constants for P, I, D, and F
     * @param Kp the proportional coefficient
     * @param Ki the integral coefficient
     * @param Kd the derivative coefficient
     * @param Kf the feed forward term
     * @param source The PIDSource object that is used to get values
     * @param output The PIDOutput object that is set to the output percentage
     */
    public SwervePIDController(double Kp, double Ki, double Kd, double Kf,
            PIDSource source, PIDOutput output){

        if (source == null) {
            throw new NullPointerException("Null PIDSource was given");
        }
        if (output == null) {
            throw new NullPointerException("Null PIDOutput was given");
        }

        m_P = Kp;
        m_I = Ki;
        m_D = Kd;
        m_F = Kf;

        m_pidInput = source;
        m_pidOutput = output;

        m_tolerance = new NullTolerance();
    }

    
    /**
     * Allocate a PID object with the given constants for P, I, D and period
     * @param Kp
     * @param Ki
     * @param Kd
     * @param source
     * @param output
     * @param period 
     */
    public SwervePIDController(double Kp, double Ki, double Kd,
            PIDSource source, PIDOutput output) {
        this(Kp, Ki, Kd, 0.0, source, output);
    }

    /**
     * Read the input, calculate the output accordingly, and write to the output.
     * This should only be called by the PIDTask
     * and is created during initialization.
     */
    protected void calculate() {
        boolean enabled;
        PIDSource pidInput;

        synchronized (this) {
            if (m_pidInput == null) {
                return;
            }
            if (m_pidOutput == null) {
                return;
            }
            enabled = m_enabled; // take snapshot of these values...
            pidInput = m_pidInput;
        }

        if (enabled) {
            double input = pidInput.pidGet();
            double result;
            PIDOutput pidOutput = null;

            synchronized (this) {
                m_error = m_setpoint - input;
                if (m_continuous) {
                    if (Math.abs(m_error)
                            > (m_maximumInput - m_minimumInput) / 2) {
                        if (m_error > 0) {
                            m_error = m_error - m_maximumInput + m_minimumInput;
                        } else {
                            m_error = m_error
                                    + m_maximumInput - m_minimumInput;
                        }
                    }
                }

                if (m_I != 0)
                {
                        double potentialIGain = (m_totalError + m_error) * m_I;
                        if (potentialIGain < m_maximumOutput)
                        {
                                if (potentialIGain > m_minimumOutput) {
                m_totalError += m_error;
                }
                                else {
                m_totalError = m_minimumOutput / m_I;
                }
                        }
                        else
                        {
                                m_totalError = m_maximumOutput / m_I;
                        }
                }

                m_result = m_P * m_error + m_I * m_totalError + m_D * (m_error - m_prevError) + m_setpoint * m_F;
                m_prevError = m_error;

                if (m_result > m_maximumOutput) {
                    m_result = m_maximumOutput;
                } else if (m_result < m_minimumOutput) {
                    m_result = m_minimumOutput;
                }
                pidOutput = m_pidOutput;
                result = m_result;
            }

            pidOutput.pidWrite(result);
        }
    }

    /**
     * Set the PID Controller gain parameters.
     * Set the proportional, integral, and differential coefficients.
     * @param p Proportional coefficient
     * @param i Integral coefficient
     * @param d Differential coefficient
     */
    public void setPID(double p, double i, double d) {
        m_P = p;
        m_I = i;
        m_D = d;
    }

        /**
     * Set the PID Controller gain parameters.
     * Set the proportional, integral, and differential coefficients.
     * @param p Proportional coefficient
     * @param i Integral coefficient
     * @param d Differential coefficient
     * @param f Feed forward coefficient
     */
    public void setPID(double p, double i, double d, double f) {
        m_P = p;
        m_I = i;
        m_D = d;
        m_F = f;
    }
    
    /**
     * Get the Proportional coefficient
     * @return proportional coefficient
     */
    public double getP() {
        return m_P;
    }

    /**
     * Get the Integral coefficient
     * @return integral coefficient
     */
    public double getI() {
        return m_I;
    }

    /**
     * Get the Differential coefficient
     * @return differential coefficient
     */
    public double getD() {
        return m_D;
    }
    
    /**
     * Get the Feed forward coefficient
     * @return feed forward coefficient
     */
    public double getF() {
        return m_F;
    }

    /**
     * Return the current PID result
     * This is always centered on zero and constrained the the max and min outs
     * @return the latest calculated output
     */
    public double get() {
        return m_result;
    }

    /**
     *  Set the PID controller to consider the input to be continuous,
     *  Rather then using the max and min in as constraints, it considers them to
     *  be the same point and automatically calculates the shortest route to
     *  the setpoint.
     * @param continuous Set to true turns on continuous, false turns off continuous
     */
    public void setContinuous(boolean continuous) {
        m_continuous = continuous;
    }

    /**
     *  Set the PID controller to consider the input to be continuous,
     *  Rather then using the max and min in as constraints, it considers them to
     *  be the same point and automatically calculates the shortest route to
     *  the setpoint.
     */
    public void setContinuous() {
        this.setContinuous(true);
    }

    /**
     * Sets the maximum and minimum values expected from the input.
     * @param minimumInput the minimum percentage expected from the input
     * @param maximumInput the maximum percentage expected from the output
     */
    public void setInputRange(double minimumInput, double maximumInput) {
        if (minimumInput > maximumInput) {
            throw new BoundaryException("Lower bound is greater than upper bound");
        }
        m_minimumInput = minimumInput;
        m_maximumInput = maximumInput;
        setSetpoint(m_setpoint);
    }

    /**
     * Sets the minimum and maximum values to write.
     * @param minimumOutput the minimum percentage to write to the output
     * @param maximumOutput the maximum percentage to write to the output
     */
    public void setOutputRange(double minimumOutput, double maximumOutput) {
        if (minimumOutput > maximumOutput) {
            throw new BoundaryException("Lower bound is greater than upper bound");
        }
        m_minimumOutput = minimumOutput;
        m_maximumOutput = maximumOutput;
    }

    /**
     * Set the setpoint for the PIDController
     * @param setpoint the desired setpoint
     */
    public void setSetpoint(double setpoint) {
        if (m_maximumInput > m_minimumInput) {
            if (setpoint > m_maximumInput) {
                m_setpoint = m_maximumInput;
            } else if (setpoint < m_minimumInput) {
                m_setpoint = m_minimumInput;
            } else {
                m_setpoint = setpoint;
            }
        } else {
            m_setpoint = setpoint;
        }
    }

    /**
     * Returns the current setpoint of the PIDController
     * @return the current setpoint
     */
    public double getSetpoint() {
        return m_setpoint;
    }

    /**
     * Returns the current difference of the input from the setpoint
     * @return the current error
     */
    public double getError() {
        //return m_error;
        return getSetpoint() - m_pidInput.pidGet();
    }

    /**
     * Set the absolute error which is considered tolerable for use with
     * OnTarget. 
     * @param absolute error which is tolerable in the units of the input object
     */
    public void setAbsoluteTolerance(double absvalue) {
        m_tolerance = new AbsoluteTolerance(absvalue);
    }
    
    /**
     * Set the percentage error which is considered tolerable for use with
     * OnTarget. (Input of 15.0 = 15 percent)
     * @param percent error which is tolerable
     */
    public void setPercentTolerance(double percentage) {
        m_tolerance = new PercentageTolerance(percentage);
    }

    /**
     * Return true if the error is within the percentage of the total input range,
     * determined by setTolerance. This assumes that the maximum and minimum input
     * were set using setInput.
     * @return true if the error is less than the tolerance
     */
    public boolean onTarget() {
        return m_tolerance.onTarget();
    }

    /**
     * Begin running the PIDController
     */
    public void enable() {
        m_enabled = true;
    }

    /**
     * Stop running the PIDController, this sets the output to zero before stopping.
     */
    public void disable() {
        m_pidOutput.pidWrite(0);
        m_enabled = false;
    }

    /**
     * Return true if PIDController is enabled.
     */
    public boolean isEnable() {
        return m_enabled;
    }

    /**
     * Reset the previous error,, the integral term, and disable the controller.
     */
    public void reset() {
        disable();
        m_prevError = 0;
        m_totalError = 0;
        m_result = 0;
    }
    
}
