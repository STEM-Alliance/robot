package org.wfrobotics.reuse.hardware.sensors;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
/**
 * Class to use a Magnetic Potentiometer through a Talon SRX
 * like an analog potentiometer, when it doesn't have the
 * full range of 0 to 1.
 * Created for using the 6127V1A360L.5FS (987-1393-ND on DigiKey)
 */
public class MagnetoPotSRX extends MagnetoPot
{
    public TalonSRX m_Talon;

    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     */
    public MagnetoPotSRX(TalonSRX talon)
    {
        this(talon, 1);
    }

    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     * @param max full range to scale output to (360 would give output of 0-360)
     */
    public MagnetoPotSRX(TalonSRX talon, double max)
    {
        this(talon, max, 0);
    }

    /**
     * Initialize a new Magnetic Potentiometer through the SRX data port
     * @param talon TalonSRX object
     * @param max full range to scale output to (360 would give output of 0-360)
     * @param min offset to scale output to (180 would give output of 180-360)
     */
    public MagnetoPotSRX(TalonSRX talon, double max, double min)
    {
        super(max, min);

        m_Talon = talon;
        m_Talon.configSelectedFeedbackSensor(FeedbackDevice.Analog,0,0);
    }

    public double getRawInput()
    {
        return m_Talon.getSensorCollection().getAnalogInRaw() / 1023.0;
    }

    public void free() { }
}
