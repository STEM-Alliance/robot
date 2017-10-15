package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.reuse.hardware.sensors.MagnetoPot;
import org.wfrobotics.reuse.hardware.sensors.MagnetoPotSRX;
import org.wfrobotics.reuse.utilities.Utilities;

public class AngleMotorMagPot extends AngleMotor
{
    private MagnetoPot pot;

    public AngleMotorMagPot(String name, int talonAddress)
    {
        super(name, talonAddress);

        pot = new MagnetoPotSRX(motor, 360);
    }

    public void set(double speed)
    {
        double invert = angleInverted ? -1 : 1;
        motor.set(invert * speed);
    }

    public double getDegrees()
    {
        double invert = angleInverted ? 1 : -1;

        return round(Utilities.wrapToRange(invert * pot.get(), -180, 180), 2);
    }

    public void setSensorOffset(double degrees)
    {
        pot.setOffset(degrees);
    }

    /**
     * Round a value to the desired number of decimal places
     * 
     * @param d
     *            value to round
     * @param res
     *            number of decimal places
     * @return
     */
    public double round(double d, int res)
    {
        int x = (int) Math.pow(10, res);
        return Math.rint(d * x) / x;
    }
}