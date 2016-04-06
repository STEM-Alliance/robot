package org.wfrobotics.hardware;

import edu.wpi.first.wpilibj.AnalogPotentiometer;

/**
 * Class to use a Magnetic Potentiometer like an analog
 * potentiometer, when it doesn't have the full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 */
public class MagnetoPotAnalog extends MagnetoPot{
    
    public AnalogPotentiometer m_Analog;
    
    /**
     * Initialize a new Magnetic Potentiometer
     * @param channel Analog input channel
     */
    public MagnetoPotAnalog(int channel)
    {
        this(channel, 1);
    }

    /**
     * Initialize a new Magnetic Potentiometer
     * @param channel Analog input channel
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     */
    public MagnetoPotAnalog(int channel, double fullRange)
    {
        this(channel, fullRange, 0);
    }

    /**
     * Initialize a new Magnetic Potentiometer
     * @param channel Analog input channel
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     * @param offset offset to scale output to (180 would give output of 180-360)
     */
    public MagnetoPotAnalog(int channel, double fullRange, double offset)
    {
        super(fullRange, offset);
        
        m_Analog = new AnalogPotentiometer(channel);
    }

    protected double getRawInput()
    {
        return m_Analog.get();
    }
    
    public void free()
    {
        m_Analog.free();
    }
    
}
