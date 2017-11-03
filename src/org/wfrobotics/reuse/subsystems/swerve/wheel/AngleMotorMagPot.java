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

    public double getDegrees()
    {
        double invert = angleInverted ? 1 : -1;
        return round(Utilities.wrapToRange(invert * pot.get(), -180, 180), 2);
    }

    public void setSensorOffset(double degrees)
    {
        pot.setOffset(degrees);
    }
}