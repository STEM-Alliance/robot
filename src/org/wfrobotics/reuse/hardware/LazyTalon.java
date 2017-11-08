package org.wfrobotics.reuse.hardware;

import com.ctre.CANTalon;

public class LazyTalon extends CANTalon
{
    CANTalon.TalonControlMode mode;
    double last;

    public LazyTalon(int address)
    {
        super(address);
        last = Double.NaN;  // First set() never lazy
        mode = getControlMode();
    }

    @Override
    public void set(double val)
    {
        if (val != last || mode != getControlMode())
        {
            super.set(val);
            last = val;
        }
    }
}
