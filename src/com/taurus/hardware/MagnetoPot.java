package com.taurus.hardware;

import com.taurus.Utilities;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;

/**
 * Class to use a Magnetic Potentiometer like an analog
 * potentiometer, when it doesn't have the full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 */
public class MagnetoPot extends AnalogPotentiometer {

    private double InMin = 0.041; // measured from raw sensor input
    private double InMax = 0.961; // measured from raw sensor input

    private double fullRange;
    private double offset;
    
    /**
     * Initialize a new Magnetic Potentiometer
     * @param channel Analog input channel
     */
    public MagnetoPot(int channel)
    {
        super(channel);
        this.fullRange = 1;
        this.offset = 0;
    }

    /**
     * Initialize a new Magnetic Potentiometer
     * @param input Analog input object
     */
    public MagnetoPot(AnalogInput input)
    {
        super(input);
        this.fullRange = 1;
        this.offset = 0;
    }

    /**
     * Initialize a new Magnetic Potentiometer
     * @param channel Analog input channel
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     */
    public MagnetoPot(int channel, double fullRange)
    {
        super(channel);
        this.fullRange = fullRange;
        this.offset = 0;
    }

    /**
     * Initialize a new Magnetic Potentiometer
     * @param input Analog input object
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     */
    public MagnetoPot(AnalogInput input, double fullRange)
    {
        super(input);
        this.fullRange = fullRange;
        this.offset = 0;
    }

    /**
     * Initialize a new Magnetic Potentiometer
     * @param channel Analog input channel
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     * @param offset offset to scale output to (180 would give output of 180-360)
     */
    public MagnetoPot(int channel, double fullRange, double offset)
    {
        super(channel);
        this.fullRange = fullRange;
        this.offset = offset;
    }

    /**
     * Initialize a new Magnetic Potentiometer
     * @param input Analog input object
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     * @param offset offset to scale output to (180 would give output of 180-360)
     */
    public MagnetoPot(AnalogInput input, double fullRange, double offset)
    {
        super(input);
        this.fullRange = fullRange;
        this.offset = offset;
    }

    /**
     * Get the scaled value of the sensor 
     * @return value from offset to fullRange
     */
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
    
    /**
     * Get the raw value of the sensor
     * @return value from 0 to 1
     */
    public double getNormal()
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
        return Utilities.scaleToRange(val, InMin, InMax, 0, 1);
    }

}
