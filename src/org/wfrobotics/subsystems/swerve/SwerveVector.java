/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.wfrobotics.subsystems.swerve;

import org.wfrobotics.Utilities;

/**
 * Auto calculate angle and magnitude/angular velocity/distance from x and y
 * values
 * 
 * @author Team 4818 WFRobotics
 */
public class SwerveVector {
    
    private double mag;
    private double ang;

    /**
     * empty constructor
     */
    public SwerveVector()
    {
        mag = 0;
        ang = 0;
    }

    /**
     * Constructor from x and y values
     * 
     * @param position
     *            values as array
     */
    public SwerveVector(double[] position)
    {
        this.setXY(position[0], position[1]);
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
        this.setXY(x, y);
    }

    /**
     * Constructor from magnitude and angle degrees
     * 
     * @param mag
     *            magnitude of vector
     * @param angle
     *            angle of vector
     */
    public static SwerveVector NewFromMagAngle(double mag, double ang)
    {
        SwerveVector r = new SwerveVector();
        r.mag = mag;
        r.ang = ang;
        return r;
    }
    
    public void setXY(double x, double y)
    {
        this.mag = Math.sqrt(x * x + y * y);
        this.ang = Math.toDegrees(Math.atan2(y, x));
    }
    
    /**
     * Set x and y from magnitude and angle degrees
     * 
     * @param mag
     *            magnitude of vector
     * @param angle
     *            angle of vector
     */
    public void setMagAngle(double mag, double ang)
    {
        this.mag = mag;
        this.ang = ang;
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
        double realAngle = Math.toRadians(Utilities.wrapToRange(ang, 0, 360));
        return Math.cos(realAngle) * mag;
    }

    /**
     * Get the y value
     * 
     * @return
     */
    public double getY()
    {
        double realAngle = Math.toRadians(Utilities.wrapToRange(ang, 0, 360));
        return Math.sin(realAngle) * mag;
    }

    /**
     * Get the magnitude of the vector
     * 
     * @return
     */
    public double getMag()
    {
        return mag;
    }

    /**
     * Get the angle of the vector in degrees
     * 
     * @return
     */
    public double getAngle()
    {
        return ang;
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
