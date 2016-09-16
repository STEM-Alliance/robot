package org.wfrobotics.hardware;

import org.wfrobotics.CircularBuffer;
import org.wfrobotics.Utilities;

import edu.wpi.first.wpilibj.Timer;

/**
 * Class to use a Magnetic Potentiometer like an analog
 * potentiometer, when it doesn't have the full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 */
public abstract class MagnetoPot {

    private double InMin = 0.041; // measured from raw sensor input
    private double InMax = 0.961; // measured from raw sensor input

    private double fullRange;
    private double offset;

    private boolean average = false;
    private CircularBuffer averageBuff;
    private double averageLastTime = 0;
    private double lastAverage = 0;
    
    /**
     * Initialize a new Magnetic Potentiometer
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     * @param offset offset to scale output to (180 would give output of 180-360)
     */
    public MagnetoPot(double fullRange, double offset)
    {
        this.fullRange = fullRange;
        this.offset = offset;
    }


    protected abstract double getRawInput();
    
    protected double getValue()
    {
        double val = getRawInput();
        
        if(average)
        {
            if((Timer.getFPGATimestamp() - averageLastTime) > .01)
            {
                averageBuff.pushFront(val);
                averageLastTime = Timer.getFPGATimestamp();
                
                val = averageBuff.getAverage();
                lastAverage = val;
            }
            else
            {
                val = lastAverage;
            }
        }
        
        return val;
    }
    
    /**
     * Get the scaled value of the sensor 
     * @return value from offset to fullRange
     */
    public double getWithoutOffset()
    {
        // convert to 0-1 scale
        double val = getValue();
        
        // update the values if needed
        if (val > InMax)
        {
            InMax = val;
        }
        if (val < InMin)
        {
            InMin = val;
        }

        // scale it based on the calibration values
        return Utilities.scaleToRange(val, InMin, InMax, 0, fullRange);
    }

    /**
     * Get the scaled value of the sensor 
     * @return value from offset to (offset + fullRange)
     */
    public double get()
    {
        return getWithoutOffset() + offset;
    }
    
    /**
     * Get the scaled value of the sensor
     * @return value from 0 to fullRange
     */
    public double getWrapped()
    {
        return Utilities.wrapToRange(get(), 0, fullRange);
    }
    
    /**
     * Get the raw value of the sensor
     * @return value from 0 to 1
     */
    public double getNormal()
    {
        double val = getValue();
        
        // update the values if needed
        if (val > InMax)
        {
            InMax = val;
        }
        if (val < InMin)
        {
            InMin = val;
        }

        // scale it based on the calibration values
        return Utilities.scaleToRange(val, InMin, InMax, 0, 1);
    }

    public void setFullRange(double fullRange)
    {
        this.fullRange = fullRange;
    }
    
    public void setOffset(double offset)
    {
        this.offset = offset;
    }
    
    public void setAverage(boolean average, int size)
    {
        this.average = average;
        this.averageBuff = new CircularBuffer(size);
        
        double val = getRawInput();
        
        for (int i = 0; i < size; i++)
        {
            this.averageBuff.pushFront(val);
        }
        lastAverage = val;
        
    }

    public abstract void free();
}
