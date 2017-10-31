package org.wfrobotics.reuse.hardware;

import com.ctre.CANTalon;

public class LazyTalon extends CANTalon
{
    double last;

    public LazyTalon(int address)
    {
        super(address);
        last = Double.NaN;  // First set() never lazy
    }

    @Override
    public void set(double val)
    {
        if (val != last)
        {
            super.set(val);
            last = val;
        }
    }
}
