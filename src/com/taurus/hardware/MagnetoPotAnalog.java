package com.taurus.hardware;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

/**
 * Class to use a Magnetic Potentiometer like an analog
 * potentiometer, when it doesn't have the full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 */
public class MagnetoPotAnalog extends MagnetoPot {

    AnalogPotentiometer m_pot;
   
    /**
     * Initialize a new Magnetic Potentiometer
     * @param channel Analog input channel
     */
    public MagnetoPotAnalog(int channel)
    {
        this(channel, 1, 0, false, 0);
    }

    /**
     * Initialize a new Magnetic Potentiometer
     * @param channel Analog input channel
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     */
    public MagnetoPotAnalog(int channel, double fullRange)
    {
        this(channel, fullRange, 0, false, 0);
    }
    
    /**
     * Initialize a new Magnetic Potentiometer
     * @param channel Analog input channel
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     * @param offset offset to scale output to (180 would give output of 180-540)
     */
    public MagnetoPotAnalog(int channel, double fullRange, double offset)
    {
        this(channel, fullRange, offset, false, 0);
    }


    /**
     * Initialize a new Magnetic Potentiometer
     * @param channel Analog input channel
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     * @param offset offset to scale output to (180 would give output of 180-540)
     */
    public MagnetoPotAnalog(int channel, double fullRange, double offset, boolean average, int averageSize)
    {
        super(fullRange, offset, average, averageSize);

        m_pot = new AnalogPotentiometer(channel);
        
        fillAverage();
    }

    protected double getRaw()
    {
        return m_pot.get();
    }

}
