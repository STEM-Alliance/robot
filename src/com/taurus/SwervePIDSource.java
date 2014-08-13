/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.taurus;
import edu.wpi.first.wpilibj.PIDSource;

/**
 *
 * @author Taurus Robotics
 */
public class SwervePIDSource implements PIDSource{
    private double PIDValue = 0;
    
    public void pidSet(double val)
    {
        PIDValue = val;
    }
    
    public double pidGet()
    {
        return PIDValue;
    }
    
}
