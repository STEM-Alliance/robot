/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taurus;

import com.sun.squawk.util.MathUtils;

/**
 *
 * @author Taurus Robotics
 * auto calculate angle and hypotenuse/angular velocity/distance from x and y values
 */
public class SwervePoint
{
    private double x;
    private double y;
 
    // empty constructor
    public SwervePoint()
    {
        x = 0;
        y = 0;
    }
 
    // constructor from x and y values
    public SwervePoint(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
 
    // set x and y from hypotenuse and angle degrees
    public void SetAngleHyp(double h, double angle)
    {
        x = Math.sin(Math.toRadians(angle)) * h;
        y = Math.cos(Math.toRadians(angle)) * h;
    }
 
    // public get of x value
    public double X()
    {
        return x;
    }
 
    // public get of y value
    public double Y()
    {
        return y;
    }
 
    // public get of hypotenuse
    public double H()
    {
        return Math.sqrt(x*x+y*y);
    }
 
    // public get of angle degrees
    public double Angle()
    {
        double retVal = 0;        
        if(y != 0)
        {
            retVal = Math.toDegrees(MathUtils.atan2(y, x));
        }
 
        return retVal;
    }
}
