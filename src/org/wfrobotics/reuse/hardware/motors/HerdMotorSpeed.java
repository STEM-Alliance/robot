package org.wfrobotics.reuse.hardware.motors;

import org.wfrobotics.reuse.hardware.motors.config.HerdMotorConfig;

import com.ctre.CANTalon.*;

/**
 * CANTalon motor to be used with a CTRE Magnetic Encoder in speed mode.
 * Uses the PID built into the CANTalon.
 */
public class HerdMotorSpeed extends HerdMotor {

    public HerdMotorSpeed(HerdMotorConfig configMotor)
    {
        super(configMotor);
        
        motor.setPID(configMotor.configPID.p,
                     configMotor.configPID.i,
                     configMotor.configPID.d,
                     configMotor.configPID.f,
                     configMotor.configPID.iZone,
                     configMotor.configPID.rampRate,
                     0);
        
        motor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        motor.changeControlMode(TalonControlMode.Speed);

        //TODO BDP this seems wrong
        motor.reverseSensor(configMotor.invert);
        motor.setInverted(configMotor.invert);
    }

    /**
     * Get the current speed in RPM
     * @return speed in RPM
     */
    @Override
    public double get()
    {
        //TODO BDP this seems wrong
        return getInvertedValue() * motor.getSpeed();
    }
    
    /**
     * Set the motor output to the desired speed in RPM.
     * This will also auto enable/disable the motor if the speed is 0
     * @param rpm speed in RPM
     */
    @Override
    public void set(double rpm)
    {
        super.set(rpm);
        
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
