package org.wfrobotics.reuse.subsystems.motor2;

import org.wfrobotics.reuse.subsystems.motor2.interfaces.Motor;
import org.wfrobotics.reuse.subsystems.motor2.interfaces.PID;
import org.wfrobotics.reuse.subsystems.motor2.interfaces.Sensor;

/**
 * Controls subsystem motor by sensor feedback
 * @author Team 4818 WFRobotics
 */
public class ClosedLoopMotor implements Motor, PID, Sensor
{
    private final Motor motor;
    private final PID pid;
    private final Sensor sensor;
    
    public ClosedLoopMotor(Motor motor, PID pid, Sensor sensor)
    {
        this.motor = motor;
        this.pid = pid;
        this.sensor = sensor;
    }

    public void set(double desired)
    {
        motor.set(desired);
    }

    public void setBrake(boolean enable)
    {
        motor.setBrake(enable);
    }
    
    public void update(double setpoint)
    {
        pid.update(setpoint);
    }

    public double get()
    {
        return sensor.get();
    }

    public boolean atSetpoint()
    {
        return false;
    }
}