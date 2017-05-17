package org.wfrobotics.reuse.hardware.motors2.interfaces;

/**
 * @author Team 4818 WFRobotics
 */
public interface PID 
{
    public void setControlType(ControlType mode);
    public void update(double setpoint);
}
