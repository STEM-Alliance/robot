package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.reuse.hardware.motors.HerdMotor;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Abstract controllable subsystem to provide common functionality
 */
public abstract class MotorSubsystem extends Subsystem {
    
    protected HerdMotor motor;
    
    public MotorSubsystem(HerdMotor motor)
    {
        this.motor = motor;
    }
    
    public double get()
    {
        return motor.get();
    }
    
    public void set(double value)
    {
        motor.set(value);
    }
    
    public boolean atSetpoint(double tolerance)
    {
        return motor.atSetpoint(tolerance);
    }
}
