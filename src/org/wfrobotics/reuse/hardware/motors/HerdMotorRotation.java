package org.wfrobotics.reuse.hardware.motors;

import org.wfrobotics.reuse.hardware.motors.config.HerdMotorConfig;

public abstract class HerdMotorRotation extends HerdMotor {
    
    public HerdMotorRotation(HerdMotorConfig configMotor)
    {
        super(configMotor);
    }
    
    /**
     * set the angle offset
     * @param offset
     */
    public abstract void setOffset(double offset);
}
