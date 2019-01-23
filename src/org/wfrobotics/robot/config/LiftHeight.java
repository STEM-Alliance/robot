package org.wfrobotics.robot.config;

/** All heights are relative distances the lift must move from bottom limit switch */
public enum LiftHeight
{
    HatchHigh(36.0),   // TODO Tune
    HatchMiddle(24.00),  // TODO Tune
    HatchLow(12.00);  // TODO Tune

    private final double value;

    private LiftHeight(double value) { this.value = value; }
    public double get() { return value; }
}