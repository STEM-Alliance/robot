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
 * auto calculate angle and magnitude/angular velocity/distance 
 * from x and y values 
 */
public class SwerveVector
{
    private double x;
    private double y;
 
    /**
     * empty constructor
     */
    public SwerveVector()
    {
        x = 0;
        y = 0;
    }
    
    /**
     * Constructor from x and y values
     * @param position values as array
     */
    public SwerveVector(double[] position)
    {
        x = position[0];
        y = position[1];
    }
 

    /**
     * Constructor from x and y values
     * @param x position value
     * @param y position value
     */
    public SwerveVector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Constructor from magnitude and angle degrees
     * @param mag magnitude of vector
     * @param angle angle of vector
     * @param dummy used to construct using mag & angle
     */
    public SwerveVector(double mag, double angle, boolean dummy)
    {
        x = Math.sin(Math.toRadians(angle)) * mag;
        y = Math.cos(Math.toRadians(angle)) * mag;
    }
     
    /**
     * Set x and y from magnitude and angle degrees
     * @param mag magnitude of vector
     * @param angle angle of vector
     */
    public void setMagAngle(double mag, double angle)
    {
        x = Math.sin(Math.toRadians(angle)) * mag;
        y = Math.cos(Math.toRadians(angle)) * mag;
    }
    
    /**
     * Set just the magnitude of the vector
     * @param mag
     */
    public void setMag(double mag)
    {
        setMagAngle(mag, this.A());
    }
    
    /**
     * Set just the angle of the vector
     * @param angle
     */
    public void setAngle(double angle)
    {
        setMagAngle(this.M(), angle);
    }
 
    /**
     * Get the x value
     * @return
     */
    public double X()
    {
        return x;
    }
 
    /**
     * Get the y value
     * @return
     */
    public double Y()
    {
        return y;
    }
 
    /**
     * Get the magnitude of the vector
     * @return
     */
    public double M()
    {
        return Math.sqrt(MathUtils.pow(x, 2) + MathUtils.pow(y, 2));
    }
 
    /**
     * Get the angle of the vector
     * @return
     */
    public double A()
    {
        double retVal = 0;        
        if(y != 0)
        {
            // maybe MathUtils.atan2(x, -y); 
            retVal = Math.toDegrees(MathUtils.atan2(y, x));
        }
 
        return retVal;
    }
}
