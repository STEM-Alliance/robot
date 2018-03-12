package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.reuse.utilities.Utilities;

/** Generates a set of path segments roughly emulating motion magic */
public class FauxProfile implements PathSource
{
    private final double[][] pointsL;
    private final double[][] pointsR;
    private final int length;

    public FauxProfile(int numPoints, double maxVelocityTicks)
    {
        pointsL = generate(numPoints, maxVelocityTicks);
        pointsR = generate(numPoints, maxVelocityTicks);
        length = pointsL.length;
    }

    public double[][][] get(int start, int length)
    {
        double[][][] zippedClone = new double[length][2][2];
        int end = start + length;

        for (int index = 0; index < end; index++)
        {
            zippedClone[index][0][0] = pointsL[start + index][0];
            zippedClone[index][0][1] = pointsL[start + index][1];
            zippedClone[index][1][0] = pointsR[start + index][0];
            zippedClone[index][1][1] = pointsR[start + index][1];
        }

        return zippedClone;
    }

    public int length()
    {
        return length;
    }

    private double[][] generate(int numPoints, double maxVelocityTicks)
    {
        double maxRevs = 5;
        double maxTicksPer100Ms = maxVelocityTicks;
        double[][] points = new double[numPoints][2];

        for (int index = 0; index < numPoints; index++)
        {
            double ticks = Utilities.scaleToRange(index, 0, numPoints, 0, maxRevs * 4096);
            double unitsPer100Ms = Utilities.scaleToRange((numPoints/2-(Math.abs(numPoints/2-index)))*2.0, 0, numPoints, 0, maxTicksPer100Ms);
            points[index][0] = ticks;
            points[index][1] = unitsPer100Ms;
        }
        return points;
    }
}