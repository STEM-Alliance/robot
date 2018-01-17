package org.wfrobotics.reuse.subsystems.vision.messages;

/**
 * Each identified target's data, extracted from the table
 */
public class VisionTargetInfo 
{
    public final double center_x;
    public final double center_y;
    public final double width;
    public final double height;
    
    /**
     * Create a target object
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public VisionTargetInfo(double x, double y, double w, double h)
    {
        this.center_x = x;
        this.center_y = y;
        this.width = w;
        this.height = h;
    }
    
    public double area()
    {
        return width * height;
    }
    
    //TODO add a function that returns the HerdVector from the center of the image
}