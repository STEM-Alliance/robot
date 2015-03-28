package com.taurus.robotspecific2015;

import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Victor;

import java.util.ArrayList;

public class MotorSystem {
    private ArrayList<Talon> Talons = new ArrayList<Talon>();
    private ArrayList<Victor> Victors = new ArrayList<Victor>();
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
            if(Application.ROBOT_VERSION == 0)
            {
                Talons.add(new Talon(pins[index]));
            }
            else
            {
                Victors.add(new Victor(pins[index]));
            }
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
        if(Application.ROBOT_VERSION == 0)
        {
            for (int index = 0; index < Talons.size(); index++)
            {
                Talons.get(index).setSafetyEnabled(enabled);
                Talons.get(index).setExpiration(timeout);
            }
        }
        else
        {
            for (int index = 0; index < Victors.size(); index++)
            {
                Victors.get(index).setSafetyEnabled(enabled);
                Victors.get(index).setExpiration(timeout);
            }
        }
    }

    /**
     * Set the Talons to a speed between -1 and 1
     * 
     * @param speed
     */
    public void Set(double speed)
    {
        if(Application.ROBOT_VERSION == 0)
        {
            for (int index = 0; index < Talons.size(); index++)
            {
                Talons.get(index).set(speed * Scaling.get(index));
            }
        }
        else
        {
            for (int index = 0; index < Victors.size(); index++)
            {
                Victors.get(index).set(speed * Scaling.get(index));
            }
        }
    }

    /**
     * Scales the input to Set()
     * 
     * @param motorScaling
     */
    public void SetScale(double[] motorScaling)
    {
        if(Application.ROBOT_VERSION == 0)
        {
            for (int index = 0; index < Talons.size(); index++)
            {
                Scaling.set(index, motorScaling[index]);
            }
        }
        else
        {
            for (int index = 0; index < Victors.size(); index++)
            {
                Scaling.set(index, motorScaling[index]);
            }
        }
    }
}
