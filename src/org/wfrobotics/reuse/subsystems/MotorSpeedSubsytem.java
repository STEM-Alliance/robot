package org.wfrobotics.reuse.subsystems;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

/**
 * Speed Controllable motor subsystem.
 */
public class MotorSpeedSubsytem extends MotorSubsystem {

    public MotorSpeedSubsytem(Config config)
    {
        this(config, new CANTalon(config.canID));
    }

    public MotorSpeedSubsytem(Config config, CANTalon motor_)
    {
        super(config, motor_);
        
        motor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        motor.changeControlMode(TalonControlMode.Speed);
        
        //TODO BDP this seems wrong
        motor.reverseSensor(config.invert);
        motor.setInverted(config.invert);
    }

    /**
     * Get the current speed in RPM
     * @return speed in RPM
     */
    @Override
    public double get()
    {
        //TODO BDP this seems wrong
        return config.invert() * config.pidSource.pidGet();
    }
    
    /**
     * Set the motor output to the desired speed in RPM.
     * This will also auto enable/disable the motor if the speed is 0
     * @param rpm speed in RPM
     */
    @Override
    public void set(double rpm)
    {
        motor.set(rpm);
        
        // Not enabling/disabling each iteration in case this impacts performance
        if (rpm != lastSetpoint) 
        {
            if (rpm != 0)
            {
                motor.enable();
            }
            else
            {
                motor.disable();
            }
        }
        
        lastSetpoint = rpm;
    }
}
