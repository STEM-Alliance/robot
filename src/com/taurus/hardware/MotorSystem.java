package com.taurus.hardware;

import edu.wpi.first.wpilibj.Talon;

import java.util.ArrayList;

public class MotorSystem {
    private ArrayList<Talon> Talons = new ArrayList<Talon>();
    private ArrayList<Double> Scaling = new ArrayList<Double>();

    /**
     * set a group (or one) of talon motor(s) as a full system
     * 
     * @param pins
     */
    public MotorSystem(int[] pins)
    {
        for (int index = 0; index < pins.length; index++)
        {
            Talons.add(new Talon(pins[index]));
            Scaling.add((double) 1);
        }
    }

    /**
     * Enable/disable the safety option, and set the timeout for it to trigger
     * @param enabled
     * @param timeout
     */
    public void SetSafety(boolean enabled, double timeout)
    {
        for (int index = 0; index < Talons.size(); index++)
        {
            Talons.get(index).setSafetyEnabled(enabled);
            Talons.get(index).setExpiration(timeout);
        }
    }

    /**
     * Set the Talons to a speed between -1 and 1
     * 
     * @param speed
     */
    public void Set(double speed)
    {
        for (int index = 0; index < Talons.size(); index++)
        {
            Talons.get(index).set(speed * Scaling.get(index));
        }
    }

    /**
     * Scales the input to Set()
     * 
     * @param motorScaling
     */
    public void SetScale(double[] motorScaling)
    {
        for (int index = 0; index < Talons.size(); index++)
        {
            Scaling.set(index, motorScaling[index]);
        }
    }
}
