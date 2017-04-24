package org.wfrobotics.reuse.subsystems.swerve.wheel;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.hardware.sensors.MagnetoPot;
import org.wfrobotics.reuse.hardware.sensors.MagnetoPotSRX;

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
        
        return Utilities.round(Utilities.wrapToRange(invert * pot.get(), -180, 180), 2);
    }
    
    public void setSensorOffset(double degrees)
    {
        pot.setOffset(degrees);
    }
}