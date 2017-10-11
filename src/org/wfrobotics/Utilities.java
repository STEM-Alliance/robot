package org.wfrobotics;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Basic utilities
 * 
 * @author Team 4818 WFRobotics
 *
 */
public final class Utilities {

    public static final void PrintCommand(String sub, Object command)
    {
        PrintCommand(sub, command, "");
    }


    public static final void PrintCommand(String sub, Object command, String mode)
    {
        String append = "";

        if(mode != null && !mode.isEmpty())
        {
            append = "_" + mode;
        }

        if(command != null)
        {
            String name = command.getClass().getName();
            SmartDashboard.putString(sub + " Sub", name.substring(name.lastIndexOf('.') + 1) + append);
        }
        else
        {
            SmartDashboard.putString(sub + " Sub", "");
        }
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
        if (value < min)
        {
            return min;
        }
        else if (value < max)
        {
            return value;
        }
        else
        {
            return max;
        }
    }

    /**
     * Trim a value to keep it in the min/max range.
     * 
     * @param value
     *            Input value
     * @param limit
     *            Maximum and -minimum expected output value
     * @return A value between min and max
     */
    public static final double clampToRange(double value, double limit)
    {
        return clampToRange(value, -limit, limit);
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
     * Check if a number is between two others
     * 
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static boolean isBetween(double val, double min, double max)
    {
        return max > min ? val > min && val < max : val > max && val < min;
    }

    /**
     * Round a value to the desired number of decimal places
     * 
     * @param d
     *            value to round
     * @param res
     *            number of decimal places
     * @return
     */
    public static double round(double d, int res)
    {
        int x = (int) Math.pow(10, res);
        return Math.rint(d * x) / x;
    }

    /**
     * Split up a string into an array, delimited by spaces
     * 
     * @param string
     * @param wordsPerLine
     * @return
     */
    public static String[] splitStringByWords(String string, int wordsPerLine)
    {
        String[] strings = string.split(" ");
        int last = strings.length % wordsPerLine;
        int keys = strings.length / wordsPerLine;
        if (last != 0)
        {
            keys += 1;
        }

        String[] lines = new String[keys];

        for (int i = 0; i < keys; i++)
        {
            int iteration = wordsPerLine;
            if (i == keys - 1 && last != 0)
            {
                iteration = last;
            }

            int offset = i * wordsPerLine;
            String line = "";

            for (int j = offset; j < offset + iteration; j++)
            {
                line += strings[j] + " ";
            }

            lines[i] = line;
        }
        return lines;
    }

    /**
     * Perform a deep copy of a two dimensional array. Very inefficient
     * 
     * @param arr
     * @return
     */
    public static double[][] arrayCopy(double[][] arr)
    {
        // size first dimension of array
        double[][] temp = new double[arr.length][arr[0].length];

        for (int i = 0; i < arr.length; i++)
        {
            // first dimension of array
            temp[i] = arrayCopy(arr[i]);
        }

        return temp;
    }

    /**
     * Perform a deep copy of a one dimensional array. Very inefficient
     * 
     * @param arr
     * @return
     */
    public static double[] arrayCopy(double[] arr)
    {
        double[] temp = new double[arr.length];

        for (int i = 0; i < arr.length; i++)
        {
            temp[i] = arr[i];
        }

        return temp;
    }
}
