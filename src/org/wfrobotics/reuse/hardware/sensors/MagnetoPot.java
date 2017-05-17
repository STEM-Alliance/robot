package org.wfrobotics.reuse.hardware.sensors;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.hardware.motors2.interfaces.Sensor;

/**
 * Class to use a Magnetic Potentiometer like an analog
 * potentiometer, when it doesn't have the full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 */
public abstract class MagnetoPot implements Sensor 
{
    private double RAW_MIN = 0.041; // measured from raw sensor input
    private double RAW_MAX = 0.961; // measured from raw sensor input

    private double max;
    private double min;
    private double offset;
    
    /**
     * Initialize a new Magnetic Potentiometer
     * @param max
     * @param min
     */
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
    @Override
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
        return Utilities.wrapToRange(Utilities.scaleToRange(val, RAW_MIN, RAW_MAX, min, max) + offset, min, max);
    }
        
    public void setMax(double max)
    {
        this.max = max;
    }
    
    public void setMin(double max)
    {
        this.min = max;
    }
    
    public void setOffset(double offset)
    {
        this.offset = offset;
    }


    public abstract void free();
}
