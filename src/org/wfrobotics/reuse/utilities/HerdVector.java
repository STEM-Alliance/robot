package org.wfrobotics.reuse.utilities;

/**
 * @author Team 4818 WFRobotics
 */
public class HerdVector
{
    private double mag;
    private double angle;
    
    public HerdVector(double mag, double angle)
    {
        double m = mag;
        double a = angle;
        
        if (m < 0)
        {
            m = -m;
            a = a + 180;
        }
        
        this.mag = m; 
        this.angle = ((a + 180 % 360) + 360) % 360 - 180;  // -180 to 180
    }
    
    public HerdVector(HerdVector clone) 
    {
        this(clone.mag, clone.angle);
    }
    
    public double getMag()
    {
        return mag;
    }
    
    public double getAngle()
    {
        return angle;
    }
    
    public double getX()
    {
        return mag * FastTrig.cos(angle);
    }
    
    public double getY()
    {
        return mag * FastTrig.sin(angle);
    }
    
    public double angleRelativeTo(HerdVector b)
    {
        return angleRelativeTo(b.getAngle());
    }
    
    public double angleRelativeTo(double angle)
    {
        double diff = this.angle - angle;
        return ((diff + 180 % 360) + 360) % 360 - 180;  // -180 to 180
    }
    
    public String toString()
    {
        return String.format("(%.2f, %.2f\u00b0)", mag, angle);
    }
    
    public HerdVector add(HerdVector b)
    {
        double x = this.getX() + b.getX();
        double y = this.getY() + b.getY();
        
        return new HerdVector(Math.sqrt(x * x + y * y), Math.atan2(y, x) * 180 / Math.PI);
    }
    
    public HerdVector sub(HerdVector b)
    {
        return add(b.scale(-1));
    }
    
    public HerdVector rotate(HerdVector b)
    {
        return rotate(b.getAngle());
    }
    
    public HerdVector rotate(double angle)
    {
        return new HerdVector(this.mag, this.angle + angle);
    }
    
    public HerdVector scale(HerdVector b)
    {
        return scale(b.getMag());
    }
    
    public HerdVector scale(double mag)
    {
        return new HerdVector(this.mag * mag, this.angle);
    }
    
    public HerdVector cross(HerdVector b)
    {
        return new HerdVector(this.mag * b.getMag(), this.getAngle() + b.getAngle());
    }
}