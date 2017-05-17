package org.wfrobotics.reuse.subsystems.motor2;

import org.wfrobotics.reuse.subsystems.motor2.interfaces.ControlType;
import org.wfrobotics.reuse.subsystems.motor2.interfaces.Motor;
import org.wfrobotics.reuse.subsystems.motor2.interfaces.PID;
import org.wfrobotics.reuse.subsystems.motor2.interfaces.Sensor;

/**
 * Controls subsystem motor by sensor feedback
 * @author Team 4818 WFRobotics
 */
public class HerdMotor implements Motor, PID, Sensor
{
    private final Motor motor;
    private final PID pid;
    private final Sensor sensor;
    
    public HerdMotor(Motor motor, PID pid, Sensor sensor)
    {
        this.motor = motor;
        this.pid = pid;
        this.sensor = sensor;
    }
    
    public String toString()
    {
        return sensor.toString();
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
    
    public void setControlType(ControlType mode)
    {
        sensor.setControlType(mode);
    }

    public boolean atSetpoint()
    {
        return false;
    }
}