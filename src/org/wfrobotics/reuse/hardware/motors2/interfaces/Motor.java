package org.wfrobotics.reuse.hardware.motors2.interfaces;

/**
 * @author Team 4818 WFRobotics
 */
public interface Motor 
{
    public void set(double desired);
    public void setBrake(boolean enable);    
    public void setControlType(ControlType mode);
}
