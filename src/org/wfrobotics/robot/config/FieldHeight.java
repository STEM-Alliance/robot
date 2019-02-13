package org.wfrobotics.robot.config;

/** All heights are relative distances from zero */
public enum FieldHeight
{
    HatchHigh(36.0),   // TODO Tune
    HatchMiddle(24.00),  // TODO Tune
    HatchLow(12.00);  // TODO Tune

    private final double value;

    private FieldHeight(double value) { this.value = value; }
    public double get() { return value; }
}