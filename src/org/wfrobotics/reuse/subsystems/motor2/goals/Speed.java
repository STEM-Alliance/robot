package org.wfrobotics.reuse.subsystems.motor2.goals;

/**
 * @author Team 4818 WFRobotics
 */
public class Speed implements Goal
{
    private final String NAME;
    private double SPEED;
    private double TOLERANCE;
    
    public Speed(String name, double location, double tolerance)
    {
        NAME = name;
        SPEED = location;
        TOLERANCE = tolerance;
    }

    public String toString()
    {
        return String.format("%s, %.2f RPM", NAME, SPEED);
    }
    
    public double get()
    {
        return SPEED;
    }
    
    public boolean atSetpoint(double actual)
    {
        return Math.abs(SPEED - actual) < TOLERANCE;
    }
}
