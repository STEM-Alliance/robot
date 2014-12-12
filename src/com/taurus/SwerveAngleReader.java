package com.taurus;

import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SwerveAngleReader
{
    private String name;
    private Potentiometer pot;
    // FIXME: private double zero;
    
    private static double MinIn = 0.2, MaxIn = 4.8, MinOut = -180, MaxOut = 180;
    
    public SwerveAngleReader(String name, Potentiometer pot, double zero)
    {
        this.name = name;
        this.pot = pot;
        // FIXME: this.zero = zero;
    }
    
    public double get()
    {
        double val = pot.get();
        
        SmartDashboard.putNumber(this.name + ".raw", val);
        
        // Clamp to input range.
        val = Utilities.clampToRange(val, MinIn, MaxIn);
        
        // Compute the final angle.
        double ret = Utilities.scaleToRange(val, MinIn, MaxIn, MinOut, MaxOut);
        
        // Subtract off the 'zero' value, wrapping to stay within the output range.
        // FIXME: ret -= this.zero;
        // FIXME: if (ret < MinOut)
        // FIXME:     ret += MaxOut - MinOut;
        
        SmartDashboard.putNumber(this.name + ".get", ret);
        
        return ret;
    }
}
