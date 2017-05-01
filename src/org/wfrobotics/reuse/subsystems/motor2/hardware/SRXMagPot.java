package org.wfrobotics.reuse.subsystems.motor2.hardware;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.subsystems.motor2.interfaces.Sensor;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;

public class SRXMagPot implements Sensor
{
    private final double MAX;
    private double RAW_MIN = 0.041;
    private double RAW_MAX = 0.961;
    
    private final CANTalon srx;
    
    public SRXMagPot(CANTalon hw, double max)
    {
        srx = hw;
        srx.setFeedbackDevice(FeedbackDevice.AnalogPot);
        MAX = max;
    }
    
    public double get()
    {
        double analog = srx.getAnalogInPosition() / 1023;
        
        // Over time, tune where this pot wraps
        if (analog > RAW_MAX)
        {
            RAW_MAX = analog;
        } 
        else if (analog < RAW_MIN)
        {
            RAW_MIN = analog;
        }

        analog = Utilities.scaleToRange(analog, RAW_MIN, RAW_MAX, 0, MAX);
        
        return  analog;
    }
}