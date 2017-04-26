package org.wfrobotics.reuse.hardware.motors;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.hardware.motors.config.HerdMotorConfig;
import org.wfrobotics.reuse.hardware.sensors.MagnetoPotSRX;
import org.wfrobotics.reuse.utilities.PIDController;

/**
 * CANTalon motor to be used with a MagnetoPot in rotation/angle mode.
 * Uses the PID on the roboRIO and the MagnetoPot through the SRX data port.
 */
public class HerdMotorRotationMagPot extends HerdMotorRotation {

    private PIDController pidController;
    private MagnetoPotSRX magnetoPot;
    
    public HerdMotorRotationMagPot(HerdMotorConfig configMotor)
    {
        super(configMotor);
        
        pidController = new PIDController(configMotor.configPID.p,
                                          configMotor.configPID.i,
                                          configMotor.configPID.d,
                                          configMotor.configPID.maxOutput);
        
        magnetoPot = new MagnetoPotSRX(motor);
    }

    @Override
    public void setP(double p) { pidController.setP(p); }
    @Override
    public void setI(double i) { pidController.setI(i); }
    @Override
    public void setD(double d) { pidController.setD(d); }
    
    /**
     * set the angle offset
     * @param offset
     */
    @Override
    public void setOffset(double offset)
    {
        magnetoPot.setOffset(offset);
    }
    
    /**
     * Get the current angle in degrees
     * @return angle in degrees (0-360)
     */
    @Override
    public double get()
    {
        return Utilities.round(Utilities.wrapToRange(getInvertedValue() * magnetoPot.get(), 0, 360), 2);
    }
    
    /**
     * Set the motor output to the desired angle in degrees
     * @param angle angle in degrees
     */
    @Override
    public void set(double angle)
    {
        double error = Utilities.wrapToRange(angle - get(), 0, 360);
        
        double motorvalue = pidController.update(error);
        
        super.set(motorvalue);
    }
}
