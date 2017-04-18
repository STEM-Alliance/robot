package org.wfrobotics.reuse.subsystems;

import org.wfrobotics.reuse.utilities.PIDController;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Abstract controllable subsystem to provide common functionality
 */
public abstract class MotorSubsystem extends Subsystem {

    /**
     * Configuration for the motor subsystem
     */
    public class Config {
        public String name;
        public int canID;
        
        public double p;
        public double i;
        public double d;
        public double f;
        public int iZone = 0;
        public double ramp;
        public double maxOutput;

        public boolean pidOnTalon;
        public PIDSource pidSource;
        
        public double outputVoltageMax = 12;
        public double outputVoltageNominal = 0;

        public boolean brakeMode = false;
        
        public boolean invert = false;
        
        public double tolerance = 0;
        
        public int invert()
        {
            return invert ? -1 : 1;
        }
    }
    
    protected Config config;
    protected CANTalon motor;
    protected double lastSetpoint;
    
    protected PIDController pidController;

    /**
     * setup a new motor subsystem
     * @param config
     */
    public MotorSubsystem(Config config)
    {
        this(config, new CANTalon(config.canID));
    }
    
    /**
     * setup a new motor subsystem with a custom CANTalon motor
     * @param config
     * @param motor
     */
    public MotorSubsystem(Config config, CANTalon motor)
    {
        super(config.name);
        
        this.config = config;
        this.motor = motor;

        if(config.pidOnTalon)
        {
            motor.setPID(config.p, config.i, config.d, config.f, 0, config.ramp, 0);
        }
        else
        {
            pidController = new PIDController(config.p, config.i, config.d, config.maxOutput);
        }
        
        motor.configNominalOutputVoltage(config.outputVoltageNominal, -config.outputVoltageNominal);
        motor.configPeakOutputVoltage(config.outputVoltageMax, -config.outputVoltageMax);
        
        motor.ConfigFwdLimitSwitchNormallyOpen(true);
        motor.ConfigRevLimitSwitchNormallyOpen(true);
        motor.enableForwardSoftLimit(false);
        motor.enableReverseSoftLimit(false);
        
        motor.enableBrakeMode(config.brakeMode);
     
        // Unlikely default last value such that we do something in the first call to set()
        lastSetpoint = -.000000001;
    }

    @Override
    protected void initDefaultCommand()
    {
        
    }

    public abstract double get();
    public abstract void set(double value);
    
    /**
     * if the subsystem is at the last desired setpoint,
     * using the {@link Config}'s tolerance
     * @return true if within tolerance, else false
     */
    public boolean atSetpoint()
    {
        return atSetpoint(config.tolerance);
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
