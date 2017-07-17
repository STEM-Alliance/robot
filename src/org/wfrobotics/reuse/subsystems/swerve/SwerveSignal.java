package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.reuse.utilities.HerdVector;

public class SwerveSignal
{
    public final static double HEADING_IGNORE = -1;
    
    HerdVector velocity;    // Direction
    double spin;            // Twist while moving
    double heading;         // Angle to track

    public SwerveSignal(HerdVector velocity,  double rotationalSpeed, double heading)
    {
        this.velocity = new HerdVector(velocity);
        this.spin = rotationalSpeed;
        this.heading = heading;
    }

    public SwerveSignal(HerdVector velocity,  double rotationalSpeed)
    {
        this(velocity, rotationalSpeed, HEADING_IGNORE);
    }
    
    public String toString()
    {
        if(hasHeading())
        {
            return String.format("V: %s, W: %.2f, H: %.2f", velocity, spin, heading);    
        }
        else
        {
            return String.format("V: %s, W: %.2f, H: None", velocity, spin);
        }
    }
    
    public boolean hasHeading()
    {
        return heading != HEADING_IGNORE;
    }
}
