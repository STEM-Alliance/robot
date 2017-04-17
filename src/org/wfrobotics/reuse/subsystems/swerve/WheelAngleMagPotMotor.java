package org.wfrobotics.reuse.subsystems.swerve;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.hardware.MagnetoPot;
import org.wfrobotics.reuse.hardware.MagnetoPotSRX;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.util.AllocationException;

public class WheelAngleMagPotMotor implements WheelAngleMotor
{
    private CANTalon angleMotor;
    private MagnetoPot anglePot;
    /** Invert the angle motor and sensor to swap left/right */
    private boolean angleInverted = true;
    
    public WheelAngleMagPotMotor(int talonAddress)
    {
        angleMotor = new CANTalon(talonAddress);
        //angleMotor.setVoltageRampRate(30);
        angleMotor.configNominalOutputVoltage(0, 0);
        angleMotor.configPeakOutputVoltage(11, -11);
        angleMotor.ConfigFwdLimitSwitchNormallyOpen(true);
        angleMotor.ConfigRevLimitSwitchNormallyOpen(true);
        angleMotor.enableForwardSoftLimit(false);
        angleMotor.enableReverseSoftLimit(false);
        angleMotor.enableBrakeMode(false);
        //angleMotor.configNominalOutputVoltage(SwerveWheel.MINIMUM_SPEED, -SwerveWheel.MINIMUM_SPEED);  // Hardware deadband in closed-loop modes
        //angleMotor.SetVelocityMeasurementPeriod(CANTalon.VelocityMeasurementPeriod.Period_50Ms);
        //angleMotor.SetVelocityMeasurementWindow(32);
        
        anglePot = new MagnetoPotSRX(angleMotor, 360);
    }
    
    public void set(double speed)
    {
        double invert = angleInverted ? -1 : 1;
        angleMotor.set(invert * speed);
    }

    public double getAnglePotAdjusted()
    {
        double invert = angleInverted ? 1 : -1;
        
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