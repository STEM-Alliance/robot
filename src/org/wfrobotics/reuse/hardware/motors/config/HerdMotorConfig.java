package org.wfrobotics.reuse.hardware.motors.config;

import org.wfrobotics.reuse.hardware.motors.config.PIDControllerConfig;

public class HerdMotorConfig {

    public final String name;
    public final int id;
    public PIDControllerConfig configPID;
    
    // Optional, so give default values
    public boolean invert = false;
    public double outputVoltageMax = 12;
    public double outputVoltageNominal = 0;
    public boolean brakeMode = false;

    public boolean limitSwitchSoftFwdEnabled = false;
    public boolean limitSwitchSoftRevEnabled = false;
    public double limitSwitchSoftFwd = 0;
    public double limitSwitchSoftRev = 0;
    
    public boolean limitSwitchHardFwdNormallyOpen = true;
    public boolean limitSwitchHardRevNormallyOpen = true;
    public boolean limitSwitchHardFwdEnabled = false;
    public boolean limitSwitchHardRevEnabled = false;

    public HerdMotorConfig(String name, int id, PIDControllerConfig configPID)
    {
        this.name = name;
        this.id = id;
        this.configPID = configPID;
    }

    public HerdMotorConfig invert(boolean invert) { this.invert = invert; return this; }

    public HerdMotorConfig outputVoltageMax(double outputVoltageMax) { this.outputVoltageMax = outputVoltageMax; return this; }
    public HerdMotorConfig outputVoltageNominal(double outputVoltageNominal) { this.outputVoltageNominal = outputVoltageNominal; return this; }
    public HerdMotorConfig brakeMode(boolean brakeMode) { this.brakeMode = brakeMode; return this; }
    
    public HerdMotorConfig limitSwitchSoftFwdEnabled(boolean limitSwitchSoftFwdEnabled) { this.limitSwitchSoftFwdEnabled = limitSwitchSoftFwdEnabled; return this; }
    public HerdMotorConfig limitSwitchSoftRevEnabled(boolean limitSwitchSoftRevEnabled) { this.limitSwitchSoftRevEnabled = limitSwitchSoftRevEnabled; return this; }
    public HerdMotorConfig limitSwitchSoftFwd(double limitSwitchSoftFwd) { this.limitSwitchSoftFwd = limitSwitchSoftFwd; return this; } 
    public HerdMotorConfig limitSwitchSoftRev(double limitSwitchSoftRev) { this.limitSwitchSoftRev = limitSwitchSoftRev; return this; } 
    public HerdMotorConfig limitSwitchHardFwdEnabled(boolean limitSwitchHardFwdEnabled) { this.limitSwitchHardFwdEnabled = limitSwitchHardFwdEnabled; return this; }
    public HerdMotorConfig limitSwitchHardRevEnabled(boolean limitSwitchHardRevEnabled) { this.limitSwitchHardRevEnabled = limitSwitchHardRevEnabled; return this; }
    public HerdMotorConfig limitSwitchHardFwdNormallyOpen(boolean limitSwitchHardFwdNormallyOpen) { this.limitSwitchHardFwdNormallyOpen = limitSwitchHardFwdNormallyOpen; return this; }
    public HerdMotorConfig limitSwitchHardRevNormallyOpen(boolean limitSwitchHardRevNormallyOpen) { this.limitSwitchHardRevNormallyOpen = limitSwitchHardRevNormallyOpen; return this; }
    
    //public ConfigMotor build() { return new ConfigMotor(this); }

}
