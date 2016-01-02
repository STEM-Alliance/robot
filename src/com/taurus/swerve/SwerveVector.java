/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.taurus.swerve;

import com.taurus.Utilities;

/**
 * Auto calculate angle and magnitude/angular velocity/distance from x and y
 * values
 * 
 * @author Team 4818 Taurus Robotics
 */
public class SwerveVector {
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
     * 
     * @param position
     *            values as array
     */
    public SwerveVector(double[] position)
    {
        x = position[0];
        y = position[1];
    }

    /**
     * Constructor from x and y values
     * 
     * @param x
     *            position value
     * @param y
     *            position value
     */
    public SwerveVector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructor from magnitude and angle degrees
     * 
     * @param mag
     *            magnitude of vector
     * @param angle
     *            angle of vector
     */
    public static SwerveVector NewFromMagAngle(double mag, double angle)
    {
        SwerveVector r = new SwerveVector();
        r.setMagAngle(mag, angle);
        return r;
    }

    /**
     * Set the x value
     * 
     * @param x
     */
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * Set the y value
     * 
     * @param y
     */
    public void setY(double y)
    {
        this.y = y;
    }

    /**
     * Set x and y from magnitude and angle degrees
     * 
     * @param mag
     *            magnitude of vector
     * @param angle
     *            angle of vector
     */
    public void setMagAngle(double mag, double angle)
    {
        double realAngle = Math.toRadians(Utilities.wrapToRange(angle, 0, 360));
        x = Math.cos(realAngle) * mag;
        y = Math.sin(realAngle) * mag;
    }

    /**
     * Set just the magnitude of the vector
     * 
     * @param mag
     */
    public void setMag(double mag)
    {
        setMagAngle(mag, this.getAngle());
    }

    /**
     * Set just the angle of the vector
     * 
     * @param angle
     */
    public void setAngle(double angle)
    {
        setMagAngle(this.getMag(), angle);
    }

    /**
     * Get the x value
     * 
     * @return
     */
    public double getX()
    {
        return x;
    }

    /**
     * Get the y value
     * 
     * @return
     */
    public double getY()
    {
        return y;
    }

    /**
     * Get the magnitude of the vector
     * 
     * @return
     */
    public double getMag()
    {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Get the angle of the vector
     * 
     * @return
     */
    public double getAngle()
    {
        // *-1 to help math
        return Math.toDegrees(Math.atan2(y, x));
    }
    
    /**
     * Returns the sum of this and the other vector
     * @param other
     * @return
     */
    public SwerveVector add(SwerveVector other)
    {
        return new SwerveVector(
            this.getX() + other.getX(), 
            this.getY() + other.getY());
    }
    
    /**
     * Returns the difference of this and the other vector
     * @param other
     * @return
     */
    public SwerveVector subtract(SwerveVector other)
    {
        return new SwerveVector(
            this.getX() - other.getX(), 
            this.getY() - other.getY());
    }
}
