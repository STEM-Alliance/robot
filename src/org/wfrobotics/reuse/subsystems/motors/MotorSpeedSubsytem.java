package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.reuse.hardware.motors.HerdMotorSpeed;

/**
 * Speed Controllable motor subsystem.
 */
public class MotorSpeedSubsytem extends MotorSubsystem {

    public MotorSpeedSubsytem(HerdMotorSpeed motor)
    {
        super(motor);
    }

    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
        
    }
}
