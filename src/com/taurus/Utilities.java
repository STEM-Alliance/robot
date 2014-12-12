package com.taurus;

public class Utilities
{
    public static double copySign(double magnitude, double sign)
    {
        if ((sign < 0) != (magnitude < 0))
            return -magnitude;
        return magnitude;
    }
    
    public static double clampToRange(double value, double min, double max)
    {
        return value < min ? min
             : value > max ? max
                           : value;
    }
    
    public static double scaleToRange(double value, double inMin, double inMax, double outMin, double outMax)
    {
        return value * (outMax - outMin) / (inMax - inMin) + outMin;
    }
}
