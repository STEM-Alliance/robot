package frc.robot.reuse.hardware.sensors;

import frc.robot.reuse.math.Util;

/**
 * Class to use a Magnetic Potentiometer like an analog
 * potentiometer, when it doesn't have the full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 */
public abstract class MagnetoPot
{
    private double RAW_MIN = 0.041; // measured from raw sensor input
    private double RAW_MAX = 0.961; // measured from raw sensor input

    private double max;
    private double min;
    private double offset;

    public MagnetoPot(double max, double min)
    {
        this.max = max;
        this.min = min;
    }

    public abstract double getRawInput();

    /**
     * Get the value of the sensor
     * @return value from min to max
     */
    public double get()
    {
        // convert to 0-1 scale
        double val = getRawInput();

        // update the values if needed
        if (val > RAW_MAX)
        {
            RAW_MAX = val;
        }
        if (val < RAW_MIN)
        {
            RAW_MIN = val;
        }

        // scale it based on the calibration values
        // TODO BDP this is a terrible way to do this
        return Util.wrapToRange(Util.scaleToRange(val, RAW_MIN, RAW_MAX, min, max) + offset, min, max);
    }

    public void setMax(double max)
    {
        this.max = max;
    }

    public void setMin(double min)
    {
        this.min = min;
    }

    public void setOffset(double offset)
    {
        this.offset = offset;
    }

    public abstract void free();
}
