package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.utilities.HerdAngle;
import org.wfrobotics.reuse.utilities.HerdVector;

public class SwerveSignal
{
    public final static double HEADING_IGNORE = -1;

    public HerdVector velocity;    // Direction
    public double spin;            // Twist while moving
    public HerdAngle heading;      // Angle to track

    public SwerveSignal(HerdVector velocity,  double rotationalSpeed, double heading)
    {
        this.velocity = new HerdVector(velocity);
        spin = rotationalSpeed;
        this.heading = new HerdAngle(heading);
    }

    public SwerveSignal(HerdVector velocity,  double rotationalSpeed)
    {
        this(velocity, rotationalSpeed, HEADING_IGNORE);
    }

    public SwerveSignal(HerdVector velocity)
    {
        this(velocity, 0, HEADING_IGNORE);
    }

    public SwerveSignal(SwerveSignal clone)
    {
        this(clone.velocity, clone.spin, clone.heading.getAngle());
    }

    public String toString()
    {
        if(hasHeading())
        {
            return String.format("V: %s, W: %.1f, H: %s", velocity, spin, heading);
        }
        return String.format("V: %s, W: %.1f, H: None", velocity, spin);
    }

    public boolean hasHeading()
    {
        return heading.getAngle() != HEADING_IGNORE;
    }
}
