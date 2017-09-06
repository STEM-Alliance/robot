package org.wfrobotics.reuse.subsystems.vision;

/**
 * Each identified target's data, extracted from the table
 */
public class Target 
{
    public double x = 0;
    public double y = 0;
    public double width = 0;
    public double height = 0;
    
    public Target(double x, double y, double w, double h)
    {
        this.x = x; this.y = y; this.width = w; this.height = h;
    }
}