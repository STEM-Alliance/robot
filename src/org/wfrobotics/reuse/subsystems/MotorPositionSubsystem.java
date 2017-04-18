package org.wfrobotics.reuse.subsystems;

import org.wfrobotics.reuse.utilities.PIDController;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.PIDSource;

/**
 * Position Controllable motor subsystem.
 * Still a WIP.
 */
public class MotorPositionSubsystem extends MotorSubsystem 
{

    public MotorPositionSubsystem(Config config)
    {
        this(config, new CANTalon(config.canID));
    }

    public MotorPositionSubsystem(Config config, CANTalon motor_)
    {
        super(config, motor_);
        
        motor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
        motor.changeControlMode(TalonControlMode.PercentVbus);
    }

    @Override
    public double get()
    {
        //TODO BDP this seems wrong
        return config.invert() * config.pidSource.pidGet();
    }
    
    @Override
    public void set(double value)
    {
        if(config.pidOnTalon)
        {
            motor.set(value);
        }
        else
        {
            motor.set(pidController.update(value, get()));
        }
    }
}
