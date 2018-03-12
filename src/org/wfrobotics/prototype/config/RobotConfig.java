package org.wfrobotics.prototype.config;

public class RobotConfig
{
    public final int maxVelocity = 6000;
    public final double kP = 0.0;  // Conservative pure guessing
    public final double kI = kP * 0.001 * 0;  // Conservative pure guessing
    public final double kD = kP * 1.0 * 0;  // Conservative pure guessing
}
