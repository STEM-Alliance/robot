package com.taurus;

/**
 * Basic utilities
 * @author Team 4818 Taurus Robotics
 *
 */
public final class Utilities
{
    /**
     * Ensure the value is between min/max, and if it is outside the range,
     * wrap it around.
     * @param value Input value
     * @param min Minimum expected output value
     * @param max Maximum expected output value
     * @return
     */
    public static final double wrapToRange(double value, double min, double max)
    {
        value -= min;
        value %= max - min;
        value += max - min;
        value %= max - min;
        value += min;
        return value;
    }

    /**
     * Trim a value to keep it in the min/max range.
     * @param value Input value
     * @param min Minimum expected output value
     * @param max Maximum expected output value
     * @return
     */
    public static final double clampToRange(double value, double min, double max)
    {
        return value < min ? min
             : value > max ? max
                           : value;
    }

    /**
     * Scale a value from the expected input range to the expected output range.
     * @param value Input value
     * @param inMin Minimum input value
     * @param inMax Maximum input value
     * @param outMin Minimum expected output value
     * @param outMax Maximum expected output value
     * @return
     */
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
