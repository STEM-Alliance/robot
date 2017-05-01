package org.wfrobotics.reuse.subsystems.motor2.hardware;

import org.wfrobotics.reuse.subsystems.motor2.interfaces.Motor;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

public class SRXMotor implements Motor
{
    public CANTalon srx;
    
    private SRXMotor(CANTalon hw)
    {
        srx = hw;
        
        // Defaults
        srx.changeControlMode(TalonControlMode.PercentVbus);
        srx.configNominalOutputVoltage(0, 0);
        srx.configPeakOutputVoltage(11, 11);
        srx.enableBrakeMode(false);
        // TODO more defaults
    }

    public void set(double desired)
    {
        // In SRX, the hardware is controlled by the PID
    }

    public void setBrake(boolean enable)
    {
        srx.enableBrakeMode(enable);
    }
    
    public static class Builder
    {
        public SRXMotor motor;
        
        public Builder(CANTalon srx)
        {
            motor = new SRXMotor(srx);
        }
        
        public Builder maxVolts(double val)
        {
            motor.srx.configPeakOutputVoltage(val, -val);
            return this;
        }

        public SRXMotor build()
        {
            return motor;
        }
    }
}
