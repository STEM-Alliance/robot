package com.taurus.hardware;

import com.taurus.CircularBuffer;
import com.taurus.Utilities;

import edu.wpi.first.wpilibj.Timer;

/**
 * Class to use a Magnetic Potentiometer like an analog
 * potentiometer, when it doesn't have the full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 * Scaling operates using the following equation:
 * scaleToRange(val, InMin, InMax, 0, fullRange) + offset
 */
public abstract class MagnetoPot {

    protected double m_inMin = 0.041; // measured from raw sensor input
    protected double m_inMax = 0.961; // measured from raw sensor input

    protected double m_fullRange;
    protected double m_offset;

    protected boolean m_average = false;
    protected CircularBuffer m_averageBuff;
    protected double m_averageLastTime = 0;
    protected double m_lastAverage = 0;

    protected MagnetoPot()
    {
        this(0,1);
    }
    
    protected MagnetoPot(double offset, double fullRange) 
    {
        this(offset, fullRange, false, 0);
    }

    protected MagnetoPot(double offset, double fullRange, boolean average, int averageSize)
    {
        this.m_offset = offset;
        this.m_fullRange = fullRange;
        setAverage(average, averageSize);
    }
    
    /**
     * Get the raw value from the sensor
     * @return value scaled between sensor min and sensor max
     */
    protected abstract double getRaw();
    
    /**
     * Get the averaged value from the circular buffer, if used
     * @return averaged value scaled between sensor min and sensor max
     */
    public double getValueAverage()
    {
        double val = getRaw();
        
        if(m_average)
        {
            if((Timer.getFPGATimestamp() - m_averageLastTime) > .01)
            {
                m_averageBuff.pushFront(val);
                m_averageLastTime = Timer.getFPGATimestamp();
                
                val = m_averageBuff.getAverage();
                m_lastAverage = val;
            }
            else
            {
                val = m_lastAverage;
            }
        }
        
        return val;
    }
    
    /**
     * Get the normalized value of the sensor
     * @return value from 0 to 1
     */
    public double getNormal()
    {
        double val = getValueAverage();
        
        // update the values if needed
        if (val > m_inMax)
        {
            m_inMax = val;
        }
        if (val < m_inMin)
        {
            m_inMin = val;
        }

        // scale it based on the calibration values
        return Utilities.scaleToRange(val, m_inMin, m_inMax, 0, 1);
    }
    
    /**
     * Get the value between 0 and fullRange without the offset
     * @return value between 0 and fullRange
     */
    public double getWithoutOffset()
    {
        return Utilities.scaleToRange(getNormal(), 0, 1, 0, m_fullRange);
    }
    
    /**
     * Get the value between between (0 and fullRange) + offset
     * @return
     */
    public double get()
    {
        return Utilities.wrapToRange(getWithoutOffset() + m_offset, 0, m_fullRange);
    }
    
    /**
     * set the full range
     * @param fullRange
     */
    public void setFullRange(double fullRange)
    {
        this.m_fullRange = fullRange;
    }
    
    /**
     * set the offset
     * @param offset
     */
    public void setOffset(double offset)
    {
        this.m_offset = offset;
    }
    
    /**
     * set the average
     * @param average true if average, false if only one sample
     * @param size size of circular buffer
     */
    public void setAverage(boolean average, int size)
    {
        this.m_average = average;
        this.m_averageBuff = new CircularBuffer(size);
    }
    
    public void fillAverage()
    {
        double val = (double)getRaw();
        
        for (int i = 0; i < m_averageBuff.getSize(); i++)
        {
            this.m_averageBuff.pushFront(val);
        }
        m_lastAverage = val;
    }
}
