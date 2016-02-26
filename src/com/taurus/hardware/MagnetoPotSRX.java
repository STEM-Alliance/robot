package com.taurus.hardware;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;

/**
 * Class to use a Magnetic Potentiometer through a Talon SRX
 * like an analog potentiometer, when it doesn't have the 
 * full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 */
public class MagnetoPotSRX extends MagnetoPot  {
    
    public CANTalon m_Talon;
    

    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     */
    public MagnetoPotSRX(CANTalon talon) 
    {
        this(talon, 1, 0, false, 0);
    }

    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     */
    public MagnetoPotSRX(CANTalon talon, double fullRange)
    {
        this(talon, fullRange, 0, false, 0);
    }
    
    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     * @param offset offset to scale output to (180 would give output of 180-540)
     */
    public MagnetoPotSRX(CANTalon talon, double fullRange, double offset)
    {
        this(talon, fullRange, offset, false, 0);
    }

    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     * @param offset offset to scale output to (180 would give output of 180-540)
     */
    public MagnetoPotSRX(CANTalon talon, double fullRange, double offset, boolean average, int averageSize)
    {
        super(fullRange, offset, average, averageSize);

        m_Talon = talon;
        m_Talon.setFeedbackDevice(FeedbackDevice.AnalogPot);
        
        fillAverage();
    }

    protected double getRaw()
    {
        return m_Talon.getAnalogInPosition()/1023;
    }
}
