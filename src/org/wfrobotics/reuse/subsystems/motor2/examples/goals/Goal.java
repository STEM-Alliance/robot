package org.wfrobotics.reuse.subsystems.motor2.examples.goals;

/**
 * Describe what the Herd motor should do. It wants to intelligently move to the setpoint.
 * @author Team 4818 WFRobotics
 */
public interface Goal
{
    public double get();
    public boolean atSetpoint(double actual);
}