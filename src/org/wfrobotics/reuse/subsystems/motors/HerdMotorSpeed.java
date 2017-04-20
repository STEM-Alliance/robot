package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.reuse.subsystems.motors.ConfigMotor.ConfigMotorBulider;

/**
 * CANTalon motor to be used with a CTRE Magnetic Encoder in speed mode.
 * Uses the PID built into the CANTalon.
 */
public class HerdMotorSpeed extends HerdMotor {


    public HerdMotorSpeed(ConfigMotorBulider configMotorBuilder)
    {
        this(configMotorBuilder.build());
    }
    
    public HerdMotorSpeed(ConfigMotor configMotor)
    {
        super(configMotor);
        
        setPID(configMotor.configPID.p,
               configMotor.configPID.i,
               configMotor.configPID.d,
               configMotor.configPID.f,
               configMotor.configPID.iZone,
               configMotor.configPID.rampRate,
               0);
        
        setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        changeControlMode(TalonControlMode.Speed);

        //TODO BDP this seems wrong
        reverseSensor(configMotor.invert);
        setInverted(configMotor.invert);
    }

    /**
     * Get the current speed in RPM
     * @return speed in RPM
     */
    @Override
    public double get()
    {
        //TODO BDP this seems wrong
        return getInvertedValue() * getSpeed();
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
                enable();
            }
            else
            {
                disable();
            }
        }
        
        lastSetpoint = rpm;
    }
}
