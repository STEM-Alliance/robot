package org.wfrobotics.reuse.subsystems.motor2.examples.arm;

import org.wfrobotics.reuse.subsystems.motor2.examples.goals.Goal;

import com.ctre.CANTalon;

/**
 * @author Team 4818 WFRobotics
 */
public class Top implements Goal
{
    private final String NAME = "Top";
    private double TOLERANCE;
    private double LOCATION;
    
    private CANTalon talon;
    
    public Top(CANTalon hw, double location)
    {
        this.LOCATION = location;
        this.talon = hw;
    }
    
    public String toString()
    {
        return String.format("%s, %.2f\u00b0", NAME, LOCATION);
    }
    
    public double get()
    {
        return LOCATION;
    }
    
    public boolean atSetpoint(double actual)
    {
        return Math.abs(LOCATION - actual) < TOLERANCE || talon.isFwdLimitSwitchClosed();
    }
}
