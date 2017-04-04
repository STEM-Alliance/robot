package org.wfrobotics.utilities;
/**
 * Fast, degree-based trigonometry lookup (stay in degrees, faster than the Math lib)
 * @author Team 4818 WFRobotics
 */
public abstract class FastTrig
{
    private static final double[] sine = new double[360];
    
    static
    {
        double sum = 0;
        
        for (int index = 0; index < sine.length; index++)
        {
            sine[index] = Math.sin((double) index / sine.length * Math.PI * 2);
        }
        
        // Exact Cartesian coordinates
        sine[0] = 0;
        sine[(int) (sine.length * 1 / 4)] = 1;
        sine[(int) (sine.length * 2 / 4)] = 0;
        sine[(int) (sine.length * 3 / 4)] = -1;
        
        // Condition the cache - Actually this improves performance much more than the lookup table
        for (int index = 0; index < sine.length; index++)
        {
            sum += sin(index);
        }
        System.out.println("Fast trig cache conditioned: " + (sum < .000001));  // Don't optimize me out Java!
    }
    
    public static double sin(double angle)
    {
        int nearestAngle = (int) ((angle > 0) ? (angle + 0.5d) : (angle - 0.5d));        
        int wrappedToIndexRange = ((nearestAngle % 360) + 360) % 360;
        
        return sine[wrappedToIndexRange];
    }
    
    public static double cos(double angle)
    {
        return sin(angle + 90);
    }
    
    public static void debug()
    {
        long start;
        long durMathSin;
        long durSin;
        long durCos;
        long durSin2;
        double sum = 0;
        
        start = System.nanoTime();
        for (double index = -362.49; index < 362.5; index += .5)
        {
            sum += Math.sin(index);
//            System.out.format("%.2f: %.9f%n", index, Math.sin(index));
        }
        System.out.println(sum);
        sum = 0;
        durMathSin = System.nanoTime() - start;
        
        System.out.println("Fast Sin:");
        start = System.nanoTime();
        for (double index = -362.49; index < 362.5; index += .5)
        {
            sum += sin(index);
//            System.out.format("%.2f: %.9f%n", index, sin(index));
        }
        System.out.println(sum);
        sum = 0;
        durSin = System.nanoTime() - start;

        System.out.println("Fast Cos:");
        start = System.nanoTime();
        for (double index = -2.49; index < 362.5; index += .5)
        {
            sum += sin(index);
//            System.out.format("%.2f: %.9f%n", index, cos(index));
        }
        System.out.println(sum);
        sum = 0;
        durCos = System.nanoTime() - start;
        
        System.out.println("Fast Sin 2:");
        start = System.nanoTime();
        for (double index = -362.49; index < 362.5; index += .5)
        {
            sum += sin(index);
//            System.out.format("%.2f: %.9f%n", index, sin(index));
        }
        System.out.println(sum);
        sum = 0;
        durSin2 = System.nanoTime() - start;
        
        System.out.println("Math Sin Duration: " + durMathSin / 1000 + " us");
        System.out.println("Fast Sin Duration: " + durSin / 1000 + " us");
        System.out.println("Fast Cos Duration: " + durCos / 1000 + " us");
        System.out.println("Fast Sin Duration 2: " + durSin2 / 1000 + " us");
    }
}
