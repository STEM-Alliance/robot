package org.wfrobotics.reuse.subsystems.motorcontrol;

import com.ctre.CANTalon;

public class PositionMotorPot implements Motor
{
    private final CANTalon motor;
    
    private PositionMotorPot(PositionConfig config)
    {
        motor = new CANTalon(config.address);
    }

    public double get()
    {
        return motor.get();
    }
    
    public static class PositionMotorPotConfig extends PositionConfig
    {
        public PositionMotorPotConfig(int address)
        {
            super(address);
        }

        public PositionMotorPot makeMotor()
        {
            return new PositionMotorPot(this);
        }
    }
}