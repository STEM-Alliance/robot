package org.wfrobotics.reuse.hardware.motors2.wrappers;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.hardware.motors2.interfaces.ControlType;
import org.wfrobotics.reuse.hardware.motors2.interfaces.Sensor;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;

/**
 * @author Team 4818 WFRobotics
 */
public class SRXMagPot implements Sensor
{
    private double MAX;
    private double RAW_MIN = 0.041;
    private double RAW_MAX = 0.961;
    
    private final CANTalon srx;
    
    private String stringFormat;
    
    public SRXMagPot(CANTalon hw, double max)
    {
        srx = hw;
        srx.setFeedbackDevice(FeedbackDevice.AnalogPot);
        setControlType(ControlType.OFF);
    }
    
    public String toString()
    {
        return String.format(stringFormat, get());
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

    public void setControlType(ControlType mode)
    {
        switch (mode)
        {
            case ROTATION:
                stringFormat = "%.2f\u00b0";
                MAX = 360;
                break;
            case SPEED:
                // TODO
            case OFF:
            default:
                stringFormat = "Off %.2f";
                MAX = 0;
                break;  
        }
    }
}