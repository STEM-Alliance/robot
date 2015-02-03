package com.taurus;

/**
 * Basic utilities
 * 
 * @author Team 4818 Taurus Robotics
 *
 */
public final class Utilities {
    /**
     * Ensure the value is between min and max, and if it is outside the range,
     * wrap it around.
     * 
     * @param value
     *            Input value
     * @param min
     *            Minimum expected output value
     * @param max
     *            Maximum expected output value
     * @return The value wrapped to between min and max
     */
    public static final double wrapToRange(double value, double min, double max)
    {
        // Subract off the min, wrap to the range, and add the min again
        return wrapToRange(value - min, max - min) + min;
    }

    /**
     * Ensure the value is between 0 and max, and if it is outside the range,
     * wrap it around.
     * 
     * @param value
     *            Input value
     * @param max
     *            Maximum expected output value
     * @return The value wrapped to between 0 and max
     */
    public static final double wrapToRange(double value, double max)
    {
        // The Java mod operator returns values from -max to max,
        // so we need to add max and mod again
        return ((value % max) + max) % max;
    }

    /**
     * Ensure the value is between 0 and max, and if it is outside the range,
     * wrap it around.
     * 
     * @param value
     *            Input value
     * @param max
     *            Maximum expected output value
     * @return The value wrapped to between 0 and max
     */
    public static final float wrapToRange(float value, float max)
    {
        // The Java mod operator returns values from -max to max,
        // so we need to add max and mod again
        return ((value % max) + max) % max;
    }
    /**
     * Ensure the value is between min and max, and if it is outside the range,
     * wrap it around.
     * 
     * @param value
     *            Input value
     * @param min
     *            Minimum expected output value
     * @param max
     *            Maximum expected output value
     * @return The value wrapped to between min and max
     */
    public static final float wrapToRange(float value, float min, float max)
    {
        // Subract off the min, wrap to the range, and add the min again
        return wrapToRange(value - min, max - min) + min;
    }

    /**
     * Trim a value to keep it in the min/max range.
     * 
     * @param value
     *            Input value
     * @param min
     *            Minimum expected output value
     * @param max
     *            Maximum expected output value
     * @return A value between min and max
     */
    public static final double clampToRange(double value, double min, double max)
    {
        if (value > min)
        {
            if (value < max)
            {
                return value;
            }
            else
            {
                return max;
            }
        }
        else
        {
            return min;
        }
    }

    /**
     * Scale a value from the expected input range to the expected output range.
     * 
     * @param value
     *            Input value
     * @param inMin
     *            Minimum input value
     * @param inMax
     *            Maximum input value
     * @param outMin
     *            Minimum expected output value
     * @param outMax
     *            Maximum expected output value
     * @return A value between outMin and outMax
     */
    public static final double scaleToRange(double value, double inMin,
            double inMax, double outMin, double outMax)
    {
        // Subtract off the input min value,
        // scale based on the in and out ranges,
        // and add the output min value
        return scaleToRange(value - inMin, inMax - inMin, outMax - outMin)
                + outMin;
    }

    /**
     * Scale a value from the expected input range to the expected output range.
     * 
     * @param value
     *            Input value
     * @param inMax
     *            Maximum input value
     * @param outMax
     *            Maximum expected output value
     * @return A value between 0 and outMax
     */
    public static final double scaleToRange(double value, double inMax,
            double outMax)
    {
        return clampToRange(value, 0, inMax) * outMax / inMax;
    }
    
    /**
     * Find the closest position given the input
     * @param Input
     * @param Positions
     * @return
     */
    public static double findClosest(double Input, double[] Positions)
    {
        double min = Double.MAX_VALUE;
        double closest = Input;

        for (int i = 0; i < Positions.length; i++)
        {
            final double diff = Math.abs(Positions[i] - Input);

            if (diff < min) {
                min = diff;
                closest = Positions[i];
            }
        }

        return closest;
    }
    
    /**
     * Check if a number is between two others
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static boolean isBetween(double val, double min, double max) {
        return max > min ? val > min && val < max : val > max && val < min;
    }
}
