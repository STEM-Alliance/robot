package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.reuse.subsystems.motors.ConfigMotor.ConfigMotorBulider;

public abstract class HerdMotorRotation extends HerdMotor {
    
    public HerdMotorRotation(ConfigMotorBulider configMotorBuilder)
    {
        this(configMotorBuilder.build());
    }
    
    public HerdMotorRotation(ConfigMotor configMotor)
    {
        super(configMotor);
    }
    
    /**
     * set the angle offset
     * @param offset
     */
    public abstract void setOffset(double offset);
}
