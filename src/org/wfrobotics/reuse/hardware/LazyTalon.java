package org.wfrobotics.reuse.hardware;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.ControlMode;

public class LazyTalon extends TalonSRX
{
    double last;

    public LazyTalon(int address)
    {
        super(address);
        last = Double.NaN;  // First set() never lazy
    }

    @Override
    public void set(ControlMode mode, double val)
    {
        if (val != last || mode != getControlMode())
        {
            super.set(mode, val);
            last = val;
        }
    }
}
