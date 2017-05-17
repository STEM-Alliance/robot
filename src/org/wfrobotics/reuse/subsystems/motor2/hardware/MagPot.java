package org.wfrobotics.reuse.subsystems.motor2.hardware;

import org.wfrobotics.reuse.subsystems.motor2.interfaces.ControlType;
import org.wfrobotics.reuse.subsystems.motor2.interfaces.Sensor;

/**
 * This would be our pot, NOT on an SRX, which implements the sensor interface
 * @author Team 4818 WFRobotics
 */
public class MagPot implements Sensor
{
    public double get()
    {
        return 0;
    }
    
    public void setControlType(ControlType mode)
    {
        
    }
}