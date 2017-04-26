package org.wfrobotics.reuse.hardware.motors;

import org.wfrobotics.reuse.hardware.motors.config.HerdMotorConfig;

import com.ctre.CANTalon;

public abstract class HerdMotor {

    public final String name;
    
    public double lastSetpoint;
    
    public CANTalon motor;
    
    public HerdMotor(HerdMotorConfig configMotor)
    {
        motor = new CANTalon(configMotor.id);
        
        name = configMotor.name;
        
        motor.configNominalOutputVoltage(configMotor.outputVoltageNominal, -configMotor.outputVoltageNominal);
        motor.configPeakOutputVoltage(configMotor.outputVoltageMax, -configMotor.outputVoltageMax);

        motor.enableForwardSoftLimit(configMotor.limitSwitchSoftFwdEnabled);
        motor.enableReverseSoftLimit(configMotor.limitSwitchSoftRevEnabled);
        motor.setForwardSoftLimit(configMotor.limitSwitchSoftFwd);
        motor.setReverseSoftLimit(configMotor.limitSwitchSoftRev);
        
        motor.ConfigFwdLimitSwitchNormallyOpen(configMotor.limitSwitchHardFwdNormallyOpen);
        motor.ConfigRevLimitSwitchNormallyOpen(configMotor.limitSwitchHardRevNormallyOpen);
        motor.enableLimitSwitch(configMotor.limitSwitchHardFwdEnabled, configMotor.limitSwitchHardRevEnabled);
        
        
        motor.enableBrakeMode(configMotor.brakeMode);
    }
    
    protected int getInvertedValue()
    {
        return motor.getInverted() ? -1 : 1;
    }
    
    /**
     * if the subsystem is at the last desired setpoint,
     * using the specified tolerance
     * @return true if within tolerance, else false
     */
    public boolean atSetpoint(double tolerance)
    {
        return Math.abs(lastSetpoint - motor.get()) <= tolerance;
    }

    public double get()
    {
        return motor.get();
    }
    
    public void set(double value)
    {
        motor.set(value);
    }

    public void setP(double p)
    {
        motor.setP(p);
    }

    public void setI(double i)
    {
        motor.setI(i);
    }

    public void setD(double d)
    {
        motor.setD(d);
    }
}
