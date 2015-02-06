package com.taurus;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;


/**
 * A basic PI controller for use with the Swerve drive
 * 
 * @author Team 4818 Taurus Robotics
 *
 */
public final class PIController implements LiveWindowSendable{
    // Heuristic to clear out the integral after being disabled.
    private static final double MaxTimestampDiff = 1.0;

    // Configuration.
    private double P;

    private double I;

    private final double maxOutput;

    // State.
    public double integral;
    public double lastTimestamp;

    /**
     * Create a new instance of the PIController
     * 
     * @param p
     *            proportional component
     * @param i
     *            integral component, 0 to disable
     * @param maxOutput
     *            maximum output of the controller (typically 1.0)
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
     * Update the PIController using the setpoint and the sensor reading
     * 
     * @param setpoint desired setpoint
     * @param sensor reading from the sensor
     * @param timestamp current time
     * @return new calculated output, clamped to max output range
     */
    public double update(double setpoint, double sensor, double timestamp)
    {
        return this.update(setpoint - sensor, timestamp);
    }
    
    /**
     * Update the PIController using the new error
     * 
     * @param error
     *            new error to use for calculations
     * @param timestamp
     *            current time
     * @return new calculated output, clamped to max output range
     */
    public double update(double error, double timestamp)
    {
        // Proportional term.
        double proportional = Utilities.clampToRange(error * P,
                -this.maxOutput, this.maxOutput);

        // Get time since we were last called.
        double timeDiff = timestamp - this.lastTimestamp;
        this.lastTimestamp = timestamp;

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
        }

        // Calculate output with coefficients.
        return Utilities.clampToRange(proportional + this.integral,
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
    
    public void setP(double P)
    {
        this.P = P;
    }

    public void setI(double I)
    {
        this.I = I;
    }

    @Override
    public String getSmartDashboardType()
    {
        return "PIDController";
    }

    private final ITableListener listener = new ITableListener()
    {
        @Override
        public void valueChanged(ITable table, String key, Object value,
                boolean isNew)
        {
            if (key.equals("p") || key.equals("i"))
            {
                if (getP() != table.getNumber("p", 0.0) || getI() != table.getNumber("i", 0.0))
                {
                    setP(table.getNumber("p", 0.0));
                    setI(table.getNumber("i", 0.0));
                }
            }
//            else if (key.equals("setpoint"))
//            {
//                if (getSetpoint() != ((Double) value).doubleValue())
//                    setSetpoint(((Double) value).doubleValue());
//            }
//            else if (key.equals("enabled"))
//            {
//                if (isEnable() != ((Boolean) value).booleanValue())
//                {
//                    if (((Boolean) value).booleanValue())
//                    {
//                        enable();
//                    }
//                    else
//                    {
//                        disable();
//                    }
//                }
//            }
        }
    };
    private ITable table;

    @Override
    public void initTable(ITable table)
    {
        if (this.table != null)
            this.table.removeTableListener(listener);
        this.table = table;
        if (table != null)
        {
            table.putNumber("p", getP());
            table.putNumber("i", getI());
            table.addTableListener(listener, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITable getTable()
    {
        return table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTable()
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startLiveWindowMode()
    {
        // disable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopLiveWindowMode()
    {
    }
}
