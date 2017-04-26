package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.reuse.hardware.motors.HerdMotorRotation;

/**
 * Position Controllable motor subsystem.
 */
public class MotorPositionSubsystem extends MotorSubsystem 
{

    public MotorPositionSubsystem(HerdMotorRotation config)
    {
        super(config);
    }

    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
        
    }
}
