package com.taurus;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;

/**
 * Class to use Magnetic Potentiometer like an analog potentiometer, when it doesn't
 * have the full range of 0 to 1. 
 *
 */
public class MagnetoPot extends AnalogPotentiometer {

    private double InMin = 0.041; // measured from raw sensor input
    private double InMax = 0.961; // measured from raw sensor input

    private double fullRange;
    private double offset;
    
    public MagnetoPot(int channel)
    {
        super(channel);
        this.fullRange = 1;
        this.offset = 0;
    }

    public MagnetoPot(AnalogInput input)
    {
        super(input);
        this.fullRange = 1;
        this.offset = 0;
    }

    public MagnetoPot(int channel, double scale)
    {
        super(channel);
        this.fullRange = scale;
        this.offset = 0;
    }

    public MagnetoPot(AnalogInput input, double scale)
    {
        super(input);
        this.fullRange = scale;
        this.offset = 0;
    }

    public MagnetoPot(int channel, double fullRange, double offset)
    {
        super(channel);
        this.fullRange = fullRange;
        this.offset = offset;
    }

    public MagnetoPot(AnalogInput input, double fullRange, double offset)
    {
        super(input);
        this.fullRange = fullRange;
        this.offset = offset;
    }
        
    public double get()
    {
        double val = super.get();
        
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
