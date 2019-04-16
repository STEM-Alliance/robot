package org.wfrobotics.robot.commands.drive;

import edu.wpi.first.wpilibj.Timer;

/** Proportional, Integral, Derivative */
public final class PID
{
    private final double kP;
    private final double kI;
    private final double kD;
    private final double kIZone;
    private double kDeadband;
    private double accum;
    private double errorPrev;
    private double timePrev;

    public PID(double p, double i, double d)
    {
        this(p, i, d, Double.MAX_VALUE, 0.0);
    }

    public PID(double p, double i, double d, double iZone, double deadband)
    {
        kP = p;
        kI = i;
        kD = d;
        kIZone = iZone;
        kDeadband = deadband;
        reset();
    }

    /** Input new error, returns new motor output to reduce the error based on PID constants */
    public double update(double error)
    {
        final double time = Timer.getFPGATimestamp();
        double output = 0.0;
        
        if (Math.abs(error) > kDeadband)
        {
            output += kP * error;  // TODO make P and I contributations multiplied by time too
        }
        else
        {
            accum = 0.0;
        }

        if (Double.isNaN(errorPrev) || Double.isNaN(timePrev))  // First loop after reset()
        {
            errorPrev = error;
            timePrev = time;
            return output;
        }

        double dt = timePrev - time;

        if (Math.abs(error) < kIZone || kIZone == 0.0)
        {
            accum += error;
        }
        else
        {
            accum = 0.0;
        }
        output += kI * accum + kD * (error - errorPrev) / dt;
        timePrev = time;
        errorPrev = error;
        return output;
    }

    /** Call when your setpoint (desired value) changes */
    public void reset()
    {
        accum = 0.0;
        errorPrev = Double.NaN;
        timePrev = Double.NaN;
    }
}