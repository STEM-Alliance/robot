package com.taurus.robotspecific2015;

import edu.wpi.first.wpilibj.DigitalInput;

public class SensorDigital extends Sensor {
    DigitalInput Digital; // FRC class

    /**
     * 
     * @param channel
     */
    public SensorDigital(int channel)
    {
        Digital = new DigitalInput(channel);
    }

    /**
     * {@inheritDoc}
     */
    public boolean IsOn()
    {
        return Digital.get(); // Return if digital input pin is on
    }
}
