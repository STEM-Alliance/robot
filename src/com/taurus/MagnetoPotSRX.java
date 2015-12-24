package com.taurus;

import edu.wpi.first.wpilibj.CANTalon;

/**
 * Class to use Magnetic Potentiometer through a Talon SRX
 * like an analog potentiometer, when it doesn't
 * have the full range of 0 to 1. 
 *
 */
public class MagnetoPotSRX {

    private double InMin = 0.041; // measured from raw sensor input
    private double InMax = 0.961; // measured from raw sensor input

    private double fullRange;
    private double offset;
    
    public CANTalon m_Talon;

    public MagnetoPotSRX(CANTalon talon)
    {
        m_Talon = talon;
        this.fullRange = 1;
        this.offset = 0;
    }

    public MagnetoPotSRX(CANTalon talon, double scale)
    {
        m_Talon = talon;
        this.fullRange = scale;
        this.offset = 0;
    }

    public MagnetoPotSRX(CANTalon talon, double fullRange, double offset)
    {
        m_Talon = talon;
        this.fullRange = fullRange;
        this.offset = offset;
    }
        
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

}
