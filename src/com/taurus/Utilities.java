package com.taurus;

public final class Utilities
{
    public static final double wrapToRange(double value, double min, double max)
    {
        value -= min;
        value %= max - min;
        value += max - min;
        value %= max - min;
        value += min;
        return value;
    }

    public static final double clampToRange(double value, double min, double max)
    {
        return value < min ? min
             : value > max ? max
                           : value;
    }

    public static final double scaleToRange(
            double value,
            double inMin, double inMax,
            double outMin, double outMax)
    {
        return (clampToRange(value, inMin, inMax) - inMin)
                * (outMax - outMin)
                / (inMax - inMin)
                + outMin;
    }
}
