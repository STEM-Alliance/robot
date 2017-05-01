package org.wfrobotics.reuse.subsystems.motor2.hardware;

import org.wfrobotics.reuse.subsystems.motor2.interfaces.Sensor;

/**
 * This would be our pot, NOT on an SRX, which implements the sensor interface
 */
public class MagPot implements Sensor
{
    public double get()
    {
        return 0;
    }
}