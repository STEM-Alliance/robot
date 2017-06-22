package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.utilities.HerdVector;

public class SwerveSignal
{
    HerdVector velocity;  // Direction
    double spin;          // Twist while moving
    double heading;       // Angle to track

    public SwerveSignal(HerdVector velocity,  double rotationalSpeed, double heading)
    {
        this.velocity = new HerdVector(velocity);
        this.spin = rotationalSpeed;
        this.heading = heading;
    }

    public SwerveSignal(HerdVector velocity,  double rotationalSpeed)
    {
        this(velocity, rotationalSpeed, Double.NEGATIVE_INFINITY);
    }
    
    public String toString()
    {
        return String.format("V: %s, W: %.2f, H: %.2f", velocity, spin, heading);
    }
}
