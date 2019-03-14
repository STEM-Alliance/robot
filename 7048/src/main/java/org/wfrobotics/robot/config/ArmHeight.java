package org.wfrobotics.robot.config;

/** All heights are relative distances the lift must move from bottom limit switch */
public enum ArmHeight
{
    TopLimit(49.0),   // TODO Tune
    HatchHigh(49.0),   // TODO Tune or remove
    HatchMiddle(34.6),  // TODO Tune or remove
    HatchLow(12.0),  // TODO Tune or remove
    BottomLimit(12.00);  // TODO Tune

    private final double value;

    private ArmHeight(double value) { this.value = value; }
    public double get() { return value; }
}