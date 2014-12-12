package com.taurus;

import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveAngleReader
{
    private String name;
    private Potentiometer pot;
    private double zero;
    
    //private boolean rotated180 = false;
    private double lastVal = 0;
    
    private static double MinIn = 0.0, MaxIn = 4.9, MinOut = -180, MaxOut = 180;
    
    public SwerveAngleReader(String name, Potentiometer pot, double zero)
    {
        this.name = name;
        this.pot = pot;
        this.zero = zero;
    }
    
    public double get()
    {
        double val = pot.get();
        
        SmartDashboard.putNumber(this.name + ".raw", val);
        
        // Clamp to input range.
        val = Math.max(MinIn, Math.min(MaxIn, val));
        
        // Detect crossing the end.
        /*if (Math.abs(val - this.lastVal) > (MaxIn - MinIn) / 2)
        {
            this.rotated180 = !this.rotated180;
        }*/
        
        //SmartDashboard.putBoolean(this.name + ".rotated180", this.rotated180);
        
        this.lastVal = val;
        
        // Compute the final angle.
        double ret = (val / (MaxIn - MinIn)) * (MaxOut - MinOut) + MinOut;
        
        // Subtract off the 'zero' value, wrapping to stay within the output range.
        // FIXME: ret -= this.zero;
        // FIXME: if (ret < MinOut)
        // FIXME:     ret += MaxOut - MinOut;
        
        SmartDashboard.putNumber(this.name + ".get", ret);
        
        return ret;
    }
}
