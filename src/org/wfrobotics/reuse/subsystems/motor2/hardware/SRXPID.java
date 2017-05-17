package org.wfrobotics.reuse.subsystems.motor2.hardware;

import org.wfrobotics.reuse.hardware.interfaces.ControlType;
import org.wfrobotics.reuse.hardware.interfaces.PID;

import com.ctre.CANTalon;

/**
 * @author Team 4818 WFRobotics
 */
public class SRXPID implements PID
{
    public CANTalon srx;
    
    public SRXPID(CANTalon hw)
    {
        srx = hw;
        
        // Defaults
        srx.setP(0);
        srx.setI(0);
        srx.setD(0);
        srx.setF(0);
        // TODO more defaults
        
        srx.set(0);
    }
    
    public void setControlType(ControlType mode)
    {
        // TODO units or whatever?
    }
    
    public void update(double setpoint)
    {
        srx.set(setpoint);
    }
    
    public static class Builder
    {
        public SRXPID motor;
        
        public Builder(CANTalon srx)
        {
            motor = new SRXPID(srx);
        }
        
        public Builder i(double val)
        {
            motor.srx.setI(val);
            return this;
        }
        
        public Builder p(double val)
        {
            motor.srx.setP(val);
            return this;
        }

        public SRXPID build()
        {
            return motor;
        }
    }
}
