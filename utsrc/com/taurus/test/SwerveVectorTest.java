package com.taurus.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.taurus.SwerveVector;

public class SwerveVectorTest {

    private static double[][] testCases = new double[][] 
    {
        //             x   y   angle magnitude
        new double[] { 0,  0,  0,    0 },
        new double[] { 1,  0,  0,    1 },
        new double[] { 0,  2,  90,   2 },
        new double[] { -3, 0,  180,  3 },
        new double[] { -3, -3, -135, 4.242 },
    };

    private double[] getArrayForSwerveVector(SwerveVector v)
    {
        return new double[] { v.getX(), v.getY(), v.getAngle(), v.getMag() };
    }

    @Test
    public void testNewFromXY()
    {
        for (double[] testCase : testCases)
        {
            SwerveVector v = new SwerveVector(testCase[0], testCase[1]);
            assertArrayEquals(testCase, getArrayForSwerveVector(v), 0.01);
        }
    }

    @Test
    public void testNewFromMagAngle()
    {
        for (double[] testCase : testCases)
        {
            SwerveVector v = SwerveVector.NewFromMagAngle(testCase[3], testCase[2]);
            assertArrayEquals(testCase, getArrayForSwerveVector(v), 0.01);
        }
    }

    @Test
    public void testSetMagAngle()
    {
        for (double[] testCase : testCases)
        {
            SwerveVector v = new SwerveVector();
            v.setMagAngle(testCase[3], testCase[2]);
            assertArrayEquals(testCase, getArrayForSwerveVector(v), 0.01);
        }
    }

}
