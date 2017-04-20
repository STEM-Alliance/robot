package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.reuse.subsystems.motors.ConfigPID.*;

public class ConfigMotor {
    // Required
    public final String name;
    public final int id;
    public ConfigPID configPID;
    
    // Optional
    public boolean invert;
    public double outputVoltageMax;
    public double outputVoltageNominal;
    public boolean brakeMode;

    public boolean limitSwitchSoftFwdEnabled;
    public boolean limitSwitchSoftRevEnabled;
    public double limitSwitchSoftFwd;
    public double limitSwitchSoftRev;
    
    public boolean limitSwitchHardFwdNormallyOpen;
    public boolean limitSwitchHardRevNormallyOpen;
    public boolean limitSwitchHardFwdEnabled;
    public boolean limitSwitchHardRevEnabled;
    
    
    public ConfigMotor(ConfigMotorBulider configMotorBuilder)
    {
        this.name = configMotorBuilder.name;
        this.id = configMotorBuilder.id;
        this.configPID = configMotorBuilder.configPID;
        this.invert = configMotorBuilder.invert;
        this.outputVoltageMax = configMotorBuilder.outputVoltageMax;
        this.outputVoltageNominal = configMotorBuilder.outputVoltageNominal;
        this.brakeMode = configMotorBuilder.brakeMode;
        
        this.limitSwitchSoftFwdEnabled = configMotorBuilder.limitSwitchSoftFwdEnabled;        
        this.limitSwitchSoftRevEnabled = configMotorBuilder.limitSwitchSoftRevEnabled; 
        this.limitSwitchSoftFwd = configMotorBuilder.limitSwitchSoftFwd;  
        this.limitSwitchSoftRev = configMotorBuilder.limitSwitchSoftRev;
        this.limitSwitchHardFwdNormallyOpen = configMotorBuilder.limitSwitchHardFwdNormallyOpen;   
        this.limitSwitchHardRevNormallyOpen = configMotorBuilder.limitSwitchHardRevNormallyOpen;   
        this.limitSwitchHardFwdEnabled = configMotorBuilder.limitSwitchHardFwdEnabled;        
        this.limitSwitchHardRevEnabled = configMotorBuilder.limitSwitchHardRevEnabled;        
    }
    
    public static class ConfigMotorBulider {
        private final String name;
        private final int id;
        private ConfigPID configPID;
        
        // Optional, so give default values
        private boolean invert = false;
        private double outputVoltageMax = 12;
        private double outputVoltageNominal = 0;
        private boolean brakeMode = false;

        private boolean limitSwitchSoftFwdEnabled = false;
        private boolean limitSwitchSoftRevEnabled = false;
        private double limitSwitchSoftFwd = 0;
        private double limitSwitchSoftRev = 0;
        
        private boolean limitSwitchHardFwdNormallyOpen = true;
        private boolean limitSwitchHardRevNormallyOpen = true;
        private boolean limitSwitchHardFwdEnabled = false;
        private boolean limitSwitchHardRevEnabled = false;

        public ConfigMotorBulider(String name, int id, ConfigPIDBuilder configPIDBuilder)
        {
            this(name, id, configPIDBuilder.build());
        }
        
        public ConfigMotorBulider(String name, int id, ConfigPID configPID)
        {
            this.name = name;
            this.id = id;
            this.configPID = configPID;
        }

        public ConfigMotorBulider invert(boolean invert) { this.invert = invert; return this; }

        public ConfigMotorBulider outputVoltageMax(double outputVoltageMax) { this.outputVoltageMax = outputVoltageMax; return this; }
        public ConfigMotorBulider outputVoltageNominal(double outputVoltageNominal) { this.outputVoltageNominal = outputVoltageNominal; return this; }
        public ConfigMotorBulider brakeMode(boolean brakeMode) { this.brakeMode = brakeMode; return this; }
        
        public ConfigMotorBulider limitSwitchSoftFwdEnabled(boolean limitSwitchSoftFwdEnabled) { this.limitSwitchSoftFwdEnabled = limitSwitchSoftFwdEnabled; return this; }
        public ConfigMotorBulider limitSwitchSoftRevEnabled(boolean limitSwitchSoftRevEnabled) { this.limitSwitchSoftRevEnabled = limitSwitchSoftRevEnabled; return this; }
        public ConfigMotorBulider limitSwitchSoftFwd(double limitSwitchSoftFwd) { this.limitSwitchSoftFwd = limitSwitchSoftFwd; return this; } 
        public ConfigMotorBulider limitSwitchSoftRev(double limitSwitchSoftRev) { this.limitSwitchSoftRev = limitSwitchSoftRev; return this; } 
        public ConfigMotorBulider limitSwitchHardFwdEnabled(boolean limitSwitchHardFwdEnabled) { this.limitSwitchHardFwdEnabled = limitSwitchHardFwdEnabled; return this; }
        public ConfigMotorBulider limitSwitchHardRevEnabled(boolean limitSwitchHardRevEnabled) { this.limitSwitchHardRevEnabled = limitSwitchHardRevEnabled; return this; }
        public ConfigMotorBulider limitSwitchHardFwdNormallyOpen(boolean limitSwitchHardFwdNormallyOpen) { this.limitSwitchHardFwdNormallyOpen = limitSwitchHardFwdNormallyOpen; return this; }
        public ConfigMotorBulider limitSwitchHardRevNormallyOpen(boolean limitSwitchHardRevNormallyOpen) { this.limitSwitchHardRevNormallyOpen = limitSwitchHardRevNormallyOpen; return this; }
        
        public ConfigMotor build() { return new ConfigMotor(this); }
        
    }
}
