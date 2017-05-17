package org.wfrobotics.reuse.hardware.motors2.wrappers;

import org.wfrobotics.reuse.hardware.motors2.interfaces.ControlType;
import org.wfrobotics.reuse.hardware.motors2.interfaces.Motor;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

/**
 * @author Team 4818 WFRobotics
 */
public class SRXMotor implements Motor
{
    public CANTalon srx;
    
    private SRXMotor(CANTalon hw)
    {
        srx = hw;
        
        // Defaults
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
    
    public void setControlType(ControlType mode)
    {
        switch (mode)
        {
            case ROTATION:
                srx.changeControlMode(TalonControlMode.Position);
                break;
            case SPEED:
                srx.changeControlMode(TalonControlMode.Speed);
                break;
            case OFF:
            default:
                srx.changeControlMode(TalonControlMode.PercentVbus);
                break;  
        }
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
