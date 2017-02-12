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
     * Find the closest position given the input
     * 
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

            if (diff < min)
            {
                min = diff;
                closest = Positions[i];
            }
        }

        return closest;
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
     * Returns the largest (closest to positive infinity) {@code int} value that
     * is less than or equal to the algebraic quotient. There is one special
     * case, if the dividend is the {@linkplain Integer#MIN_VALUE
     * Integer.MIN_VALUE} and the divisor is {@code -1}, then integer overflow
     * occurs and the result is equal to the {@code Integer.MIN_VALUE}.
     * <p>
     * Normal integer division operates under the round to zero rounding mode
     * (truncation). This operation instead acts under the round toward negative
     * infinity (floor) rounding mode. The floor rounding mode gives different
     * results than truncation when the exact result is negative.
     * <ul>
     * <li>If the signs of the arguments are the same, the results of
     * {@code floorDiv} and the {@code /} operator are the same. <br>
     * For example, {@code floorDiv(4, 3) == 1} and {@code (4 / 3) == 1}.</li>
     * <li>If the signs of the arguments are different, the quotient is negative
     * and {@code floorDiv} returns the integer less than or equal to the
     * quotient and the {@code /} operator returns the integer closest to zero.<br>
     * For example, {@code floorDiv(-4, 3) == -2}, whereas
     * {@code (-4 / 3) == -1}.</li>
     * </ul>
     * <p>
     *
     * @param x
     *            the dividend
     * @param y
     *            the divisor
     * @return the largest (closest to positive infinity) {@code int} value that
     *         is less than or equal to the algebraic quotient.
     * @throws ArithmeticException
     *             if the divisor {@code y} is zero
     * @see #floorMod(int, int)
     * @see #floor(double)
     * @since 1.8
     */
    public static int floorDiv(int x, int y)
    {
        int r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x))
        {
            r--;
        }
        return r;
    }

    /**
     * Returns the largest (closest to positive infinity) {@code long} value
     * that is less than or equal to the algebraic quotient. There is one
     * special case, if the dividend is the {@linkplain Long#MIN_VALUE
     * Long.MIN_VALUE} and the divisor is {@code -1}, then integer overflow
     * occurs and the result is equal to the {@code Long.MIN_VALUE}.
     * <p>
     * Normal integer division operates under the round to zero rounding mode
     * (truncation). This operation instead acts under the round toward negative
     * infinity (floor) rounding mode. The floor rounding mode gives different
     * results than truncation when the exact result is negative.
     * <p>
     * For examples, see {@link #floorDiv(int, int)}.
     *
     * @param x
     *            the dividend
     * @param y
     *            the divisor
     * @return the largest (closest to positive infinity) {@code long} value
     *         that is less than or equal to the algebraic quotient.
     * @throws ArithmeticException
     *             if the divisor {@code y} is zero
     * @see #floorMod(long, long)
     * @see #floor(double)
     * @since 1.8
     */
    public static long floorDiv(long x, long y)
    {
        long r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x))
        {
            r--;
        }
        return r;
    }

    /**
     * Returns the floor modulus of the {@code int} arguments.
     * <p>
     * The floor modulus is {@code x - (floorDiv(x, y) * y)}, has the same sign
     * as the divisor {@code y}, and is in the range of
     * {@code -abs(y) < r < +abs(y)}.
     *
     * <p>
     * The relationship between {@code floorDiv} and {@code floorMod} is such
     * that:
     * <ul>
     * <li>{@code floorDiv(x, y) * y + floorMod(x, y) == x}
     * </ul>
     * <p>
     * The difference in values between {@code floorMod} and the {@code %}
     * operator is due to the difference between {@code floorDiv} that returns
     * the integer less than or equal to the quotient and the {@code /} operator
     * that returns the integer closest to zero.
     * <p>
     * Examples:
     * <ul>
     * <li>If the signs of the arguments are the same, the results of
     * {@code floorMod} and the {@code %} operator are the same. <br>
     * <ul>
     * <li>{@code floorMod(4, 3) == 1}; &nbsp; and {@code (4 % 3) == 1}</li>
     * </ul>
     * <li>If the signs of the arguments are different, the results differ from
     * the {@code %} operator.<br>
     * <ul>
     * <li>{@code floorMod(+4, -3) == -2}; &nbsp; and {@code (+4 % -3) == +1}</li>
     * <li>{@code floorMod(-4, +3) == +2}; &nbsp; and {@code (-4 % +3) == -1}</li>
     * <li>{@code floorMod(-4, -3) == -1}; &nbsp; and {@code (-4 % -3) == -1 }</li>
     * </ul>
     * </li>
     * </ul>
     * <p>
     * If the signs of arguments are unknown and a positive modulus is needed it
     * can be computed as {@code (floorMod(x, y) + abs(y)) % abs(y)}.
     *
     * @param x
     *            the dividend
     * @param y
     *            the divisor
     * @return the floor modulus {@code x - (floorDiv(x, y) * y)}
     * @throws ArithmeticException
     *             if the divisor {@code y} is zero
     * @see #floorDiv(int, int)
     * @since 1.8
     */
    public static int floorMod(int x, int y)
    {
        int r = x - floorDiv(x, y) * y;
        return r;
    }

    /**
     * Returns the floor modulus of the {@code long} arguments.
     * <p>
     * The floor modulus is {@code x - (floorDiv(x, y) * y)}, has the same sign
     * as the divisor {@code y}, and is in the range of
     * {@code -abs(y) < r < +abs(y)}.
     *
     * <p>
     * The relationship between {@code floorDiv} and {@code floorMod} is such
     * that:
     * <ul>
     * <li>{@code floorDiv(x, y) * y + floorMod(x, y) == x}
     * </ul>
     * <p>
     * For examples, see {@link #floorMod(int, int)}.
     *
     * @param x
     *            the dividend
     * @param y
     *            the divisor
     * @return the floor modulus {@code x - (floorDiv(x, y) * y)}
     * @throws ArithmeticException
     *             if the divisor {@code y} is zero
     * @see #floorDiv(long, long)
     * @since 1.8
     */
    public static long floorMod(long x, long y)
    {
        return x - floorDiv(x, y) * y;
    }

    /**
     * Returns the absolute value of an {@code int} value. If the argument is
     * not negative, the argument is returned. If the argument is negative, the
     * negation of the argument is returned.
     *
     * <p>
     * Note that if the argument is equal to the value of
     * {@link Integer#MIN_VALUE}, the most negative representable {@code int}
     * value, the result is that same value, which is negative.
     *
     * @param a
     *            the argument whose absolute value is to be determined
     * @return the absolute value of the argument.
     */
    public static int abs(int a)
    {
        return (a < 0) ? -a : a;
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
     * Perform a deep copy of a three dimensional array. Very inefficient
     * 
     * @param arr
     * @return
     */
    public static double[][][] arrayCopy(double[][][] arr)
    {
        // size first dimension of array
        double[][][] temp = new double[arr.length][][];

        for (int i = 0; i < arr.length; i++)
        {
            // first dimension of array
            temp[i] = arrayCopy(arr[i]);
        }

        return temp;
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
