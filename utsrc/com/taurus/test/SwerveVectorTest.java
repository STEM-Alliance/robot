package com.taurus.test;

import junit.framework.TestCase;

import com.taurus.SwerveVector;

public class SwerveVectorTest extends TestCase
{

    private static double[][] testCases = new double[][] 
    {
        //             x   y   angle magnitude
        new double[] { 0,  0,  0,    0 },
        new double[] { 1,  0,  0,    1 },
        new double[] { 0,  2,  90,   2 },
        new double[] { -3, 0,  180,  3 },
        new double[] { -3, -3, -135, 4.242 },
    };

    private static void assertEquals(double[] expected, SwerveVector actual)
    {
        assertEquals(expected[0], actual.getX(), 0.01);
        assertEquals(expected[1], actual.getY(), 0.01);
        assertEquals(expected[2], actual.getAngle(), 0.01);
        assertEquals(expected[3], actual.getMag(), 0.01);
    }

    public void testNewFromXY()
    {
        for (int i = 0; i < testCases.length; i++)
        {
            SwerveVector v = new SwerveVector(testCases[i][0], testCases[i][1]);
            assertEquals(testCases[i], v);
        }
    }

    public void testNewFromMagAngle()
    {
        for (int i = 0; i < testCases.length; i++)
        {
            SwerveVector v = SwerveVector.NewFromMagAngle(testCases[i][3], testCases[i][2]);
            assertEquals(testCases[i], v);
        }
    }

    public void testSetMagAngle()
    {
        for (int i = 0; i < testCases.length; i++)
        {
            SwerveVector v = new SwerveVector();
            v.setMagAngle(testCases[i][3], testCases[i][2]);
            assertEquals(testCases[i], v);
        }
    }

}
