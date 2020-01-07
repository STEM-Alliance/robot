package frc.robot.reuse.math.control;

public class PID
{
    private final double kP;
    private final double kI;
    private final double kD;
    private final double kIZone;
    private double accum;
    private double errorPrev;
    private double timePrev;

    /** Proportional, Integral, Derivative */
    public PID(double p, double i, double d)
    {
        this(p, i, d, 0.0);
    }  

    /** Proportional, Integral, Derivative */
    public PID(double p, double i, double d, double iZone)
    {
        kP = p;
        kI = i;
        kD = d;
        kIZone = iZone;
        reset();
    }

    /** Input new error, returns new motor output to reduce the error based on PID constants */
    public double update(double time, double error)
    {
        double output = kP * error;

        if (Double.isNaN(errorPrev))
        {
            errorPrev = error;
            timePrev = time;
            return output;
        }

        double dt = timePrev - time;

        if (Math.abs(error) < kIZone || kIZone == 0.0)
        {
            accum += error * dt;
        }
        output += kI * accum + kD * (error - errorPrev) / dt;
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
