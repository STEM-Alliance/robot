package org.wfrobotics.reuse.subsystems.motor2.examples.goals;

/**
 * A basic rotational position/location in degrees
 * @author Team 4818 WFRobotics
 */
public class Position implements Goal
{
    private final String NAME;
    private double LOCATION;
    private double TOLERANCE;
    
    public Position(String name, double location, double tolerance)
    {
        NAME = name;
        LOCATION = location;
        TOLERANCE = tolerance;
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
        return Math.abs(LOCATION - actual) < TOLERANCE;
    }
}