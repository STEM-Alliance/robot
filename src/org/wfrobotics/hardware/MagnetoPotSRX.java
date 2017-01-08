package org.wfrobotics.hardware;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
/**
 * Class to use a Magnetic Potentiometer through a Talon SRX
 * like an analog potentiometer, when it doesn't have the 
 * full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 */
public class MagnetoPotSRX extends MagnetoPot {
    
    public CANTalon m_Talon;

    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     */
    public MagnetoPotSRX(CANTalon talon)
    {
        this(talon, 1);
    }

    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     */
    public MagnetoPotSRX(CANTalon talon, double fullRange)
    {
        this(talon, fullRange, 0);
    }
    
    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     * @param fullRange full range to scale output to (360 would give output of 0-360)
     * @param offset offset to scale output to (180 would give output of 180-360)
     */
    public MagnetoPotSRX(CANTalon talon, double fullRange, double offset)
    {
        super(fullRange, offset);
        
        m_Talon = talon;
        m_Talon.setFeedbackDevice(FeedbackDevice.AnalogPot);
    }

    public double getRawInput()
    {
        return (double)m_Talon.getAnalogInRaw()/1023.0;
    }

    @Override
    public void free()
    {
        
    }
}
