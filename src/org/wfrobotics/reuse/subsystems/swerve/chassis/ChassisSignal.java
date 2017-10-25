package org.wfrobotics.reuse.subsystems.swerve.chassis;

import org.wfrobotics.reuse.utilities.HerdVector;

/** Information to control the chassis */
public class ChassisSignal
{
    public HerdVector velocity;  // The whole chassis should move based on this
    public double spin;          // The whole chassis should rotate while moving based on this

    public ChassisSignal(HerdVector velocity,  double rotationalSpeed)
    {
        this.velocity = velocity;
        spin = rotationalSpeed;
    }

    public String toString()
    {
        return String.format("V: %s, W: %.2f", velocity, spin);
    }
}
