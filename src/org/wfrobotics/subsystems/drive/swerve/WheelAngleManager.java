package org.wfrobotics.subsystems.drive.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.hardware.MagnetoPot;
import org.wfrobotics.hardware.MagnetoPotSRX;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.util.AllocationException;

public class WheelAngleManager 
{
    private CANTalon angleMotor;
    private MagnetoPot anglePot;
    /** Invert the angle motor and sensor to swap left/right */
    private boolean angleInverted = true;
    
    public WheelAngleManager(int talonAddress)
    {
        angleMotor = new CANTalon(talonAddress);
        angleMotor.setVoltageRampRate(20);
        angleMotor.ConfigFwdLimitSwitchNormallyOpen(true);
        angleMotor.ConfigRevLimitSwitchNormallyOpen(true);
        angleMotor.enableForwardSoftLimit(false);
        angleMotor.enableReverseSoftLimit(false);
        angleMotor.enableBrakeMode(false);
        
        anglePot = new MagnetoPotSRX(angleMotor, 360);
    }
    
    public void set(double speed)
    {
        angleMotor.set(speed);
    }

    public double getAnglePotAdjusted()
    {
        double invert = angleInverted ? -1 : 1;
        
        return Utilities.round(Utilities.wrapToRange(invert * anglePot.get(),-180,180),2);
    }
    
    public void setPotOffset(double offset)
    {
        anglePot.setOffset(offset);
    }
    
    public double debugGetPotRaw()
    {
        return anglePot.getRawInput();
    }
    
    /**
     * For unit tests until we mock the pot
     */
    public void free()
    {
        try { anglePot.free(); } catch (AllocationException e) {}
    }
}