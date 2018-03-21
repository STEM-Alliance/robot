package org.wfrobotics.robot.config;

/** All heights are relative distances the lift must move from bottom limit switch */
public enum LiftHeight
{
    Scale(34),
    Intake(0.05);  // Minimum valid height

    private final double value;

    private LiftHeight(double value) { this.value = value; }
    public double get() { return value; }
}
