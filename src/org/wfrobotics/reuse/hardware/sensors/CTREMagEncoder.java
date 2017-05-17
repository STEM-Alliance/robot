package org.wfrobotics.reuse.hardware.sensors;

import org.wfrobotics.reuse.hardware.motors2.interfaces.ControlType;
import org.wfrobotics.reuse.hardware.motors2.interfaces.Sensor;

import com.ctre.CANTalon;

public class CTREMagEncoder implements Sensor 
{
    private CANTalon motor;
    
    public CTREMagEncoder(CANTalon motor)
    {
        this.motor = motor;
    }

    @Override
    public double get()
    {
        return motor.get();
    }

    @Override
    public void setControlType(ControlType mode)
    {
        // TODO Auto-generated method stub
    }
}
