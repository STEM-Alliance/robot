package com.taurus.hardware;

import com.taurus.Utilities;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;

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
    }

    /**
     * Get the scaled value of the sensor 
     * @return value from offset to fullRange
     */
    public double get()
    {
        // convert to 0-1 scale
        double val = (double)m_Talon.getAnalogInRaw()/1023;
        
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
        double val = (double)m_Talon.getAnalogInRaw()/1023;
        
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
}
