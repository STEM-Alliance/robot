package com.taurus.hardware;

import com.taurus.CircularBuffer;
import com.taurus.Utilities;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.Timer;

/**
 * Class to use a Magnetic Potentiometer through a Talon SRX
 * like an analog potentiometer, when it doesn't have the 
 * full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 */
public class MagnetoPotSRX  {

    private double InMin = 0.041; // measured from raw sensor input
    private double InMax = 0.961; // measured from raw sensor input

    private double fullRange;
    private double offset;
    
    private boolean average = false;
    private CircularBuffer averageBuff;
    private double averageLastTime = 0;
    
    public CANTalon m_Talon;
    

    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     */
    public MagnetoPotSRX(CANTalon talon)
    {
        m_Talon = talon;
        this.fullRange = 1;
        this.offset = 0;
    }

    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     */
    public MagnetoPotSRX(CANTalon talon, double fullRange)
    {
        m_Talon = talon;
        m_Talon.setFeedbackDevice(FeedbackDevice.AnalogPot);
        
        this.fullRange = fullRange;
        this.offset = 0;
        get();
        getNormal();
    }
    
    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     * @param offset offset to scale output to (180 would give output of 180-360)
     */
    public MagnetoPotSRX(CANTalon talon, double fullRange, double offset)
    {
        m_Talon = talon;
        this.fullRange = fullRange;
        this.offset = offset;
        get();
        getNormal();
    }

    private double getValue()
    {
        double val = (double)m_Talon.getAnalogInRaw()/1023;
        
        if(average)
        {
            if((Timer.getFPGATimestamp() - averageLastTime) > .01)
            {
                averageBuff.pushFront(val);
                averageLastTime = Timer.getFPGATimestamp();
            }
            val = averageBuff.getAverage();
        }
        
        return val;
    }
    
    /**
     * Get the scaled value of the sensor 
     * @return value from offset to fullRange
     */
    public double get()
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
        return Utilities.scaleToRange(val, InMin, InMax, 0, fullRange) + offset;
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
        
        double val = (double)m_Talon.getAnalogInRaw()/1023;
        
        for (int i = 0; i < size; i++)
        {
            this.averageBuff.pushFront(val);
        }
        
    }
}
