package org.wfrobotics.reuse.subsystems.motors;

import com.ctre.CANTalon;

public abstract class HerdMotor extends CANTalon {

    public final String name;
    
    public double lastSetpoint;
    
    public HerdMotor(ConfigMotor configMotor)
    {
        super(configMotor.id);
        
        name = configMotor.name;
        
        configNominalOutputVoltage(configMotor.outputVoltageNominal, -configMotor.outputVoltageNominal);
        configPeakOutputVoltage(configMotor.outputVoltageMax, -configMotor.outputVoltageMax);

        enableForwardSoftLimit(configMotor.limitSwitchSoftFwdEnabled);
        enableReverseSoftLimit(configMotor.limitSwitchSoftRevEnabled);
        setForwardSoftLimit(configMotor.limitSwitchSoftFwd);
        setReverseSoftLimit(configMotor.limitSwitchSoftRev);
        
        ConfigFwdLimitSwitchNormallyOpen(configMotor.limitSwitchHardFwdNormallyOpen);
        ConfigRevLimitSwitchNormallyOpen(configMotor.limitSwitchHardRevNormallyOpen);
        enableLimitSwitch(configMotor.limitSwitchHardFwdEnabled, configMotor.limitSwitchHardRevEnabled);
        
        
        enableBrakeMode(configMotor.brakeMode);
    }
    
    protected int getInvertedValue()
    {
        return getInverted() ? -1 : 1;
    }
    
    /**
     * if the subsystem is at the last desired setpoint,
     * using the specified tolerance
     * @return true if within tolerance, else false
     */
    public boolean atSetpoint(double tolerance)
    {
        return Math.abs(lastSetpoint - get()) <= tolerance;
    }
}
