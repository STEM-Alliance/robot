package org.wfrobotics.reuse.hardware.sensors;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class CTREMagEncoder implements PIDSource {
    
    private CANTalon motor;
    private PIDSourceType m_pidSourceType;
    
    public CTREMagEncoder(CANTalon motor)
    {
        this.motor = motor;
    }
    
    @Override
    public void setPIDSourceType(PIDSourceType pidSource)
    {
        m_pidSourceType = pidSource;
    }

    @Override
    public PIDSourceType getPIDSourceType()
    {
        return m_pidSourceType;
    }

    @Override
    public double pidGet()
    {
        return motor.get();
    }

}
