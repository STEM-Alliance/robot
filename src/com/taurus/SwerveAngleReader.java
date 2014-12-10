package com.taurus;

import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveAngleReader
{
    private String name;
    private Potentiometer pot;
    private boolean rotated180 = false;
    private double zero = 0;
    private double lastVal = 0;
    
    private static double MinIn = 0.2, MaxIn = 4.8, MinOut = -180, MaxOut = 180;
    
    public SwerveAngleReader(String name, Potentiometer pot)
    {
        this.name = name;
        this.pot = pot;
    }
    
    public void zero()
    {
        this.zero = this.lastVal = pot.get();
        this.rotated180 = false;
    }
    
    public double get()
    {
        double val = pot.get();
        
        SmartDashboard.putNumber(this.name + ".raw", val);
        
        // Clamp to input range.
        val = Math.max(MinIn, Math.min(MaxIn, val));
        
        // Subtract off the 'zero' value, wrapping to stay within the input range.
        val -= this.zero;
        if (val < MinIn)
            val += MaxIn - MinIn;
        
        // Detect crossing the end.
        if (Math.abs(val - this.lastVal) > (MaxIn - MinIn) / 2)
        {
            this.rotated180 = !this.rotated180;
        }
        
        SmartDashboard.putBoolean(this.name + ".rotated180", this.rotated180);
        
        this.lastVal = val;
        
        // Compute the final angle.
        double ret = (val / (MaxIn - MinIn) + (rotated180 ? 1 : 0)) * (MaxOut - MinOut) / 2;
        
        SmartDashboard.putNumber(this.name + ".get", ret);
        
        return ret;
    }
}
