package com.taurus.hardware;

import edu.wpi.first.wpilibj.*;

import java.util.ArrayList;

/**
 * Used to create a system of motors that work in tandem, regardless of motor controller types,
 * as well as providing a single interface to set output speeds/values
 * @author bpoppe
 *
 */
public class MotorSystem {
    
    public enum TYPES
    {
        TALON_SRX_CAN,
        TALON_SRX_PWM,
        TALON_SR_PWM,
        VICTOR_PWM,
        VICTOR_SP_PWM,
        SD540_PWM,
        SPARK_PWM;
        
        public SpeedController getController(int port)
        {
            SpeedController controller;
            
            switch(this)
            {
                case TALON_SRX_CAN:
                    controller = new CANTalon(port);
                    break;
                    
                case TALON_SRX_PWM:
                    controller = new TalonSRX(port);
                    break;
                    
                case TALON_SR_PWM:
                    controller = new Talon(port);
                    break;
                    
                case VICTOR_PWM:
                    controller = new Victor(port);
                    break;
                    
                case VICTOR_SP_PWM:
                    controller = new VictorSP(port);
                    break;
                    
                case SD540_PWM:
                    controller = new SD540(port);
                    break;
                    
                case SPARK_PWM:
                    controller = new Spark(port);
                    break;
                default:
                    controller = new CANTalon(port);
                    break;
            }
            return controller;
        }
    }
    
    private ArrayList<SpeedController> m_motors = new ArrayList<SpeedController>();
    private double m_scaling = 1.0;
    
    /**
     * Setup a new motor system, specifying ports and types for each motor
     * @param ports port numbers (CAN addresses or PWM ports)
     * @param types types of motor controllers
     */
    public MotorSystem(int[] ports, TYPES[] types)
    {
        this(ports, types, 1.0);
    }

    /**
     * Setup a new motor system, specifying ports and types for each motor
     * @param ports port numbers (CAN addresses or PWM ports)
     * @param types types of motor controllers
     * @param scaling value to scale speed setting by, 1 is no change
     */
    public MotorSystem(int[] ports, TYPES[] types, double scaling)
    {
        for (int i = 0; i < ports.length; i++)
        {
            m_motors.add(types[i].getController(ports[i]));
        }
        m_scaling = scaling;
    }

    /**
     * set the speed to the motor
     * @param speed
     */
    public void set(double speed)
    {
        for (int index = 0; index < m_motors.size(); index++)
        {
            m_motors.get(index).set(speed * m_scaling);
        }
    }

    /**
     * set the scaling for setting the speed
     * @param motorScaling value, 1 is no change
     */
    public void setScaling(double motorScaling)
    {
       m_scaling = motorScaling;
    }
    
    /**
     * get all the motor controllers
     * @return
     */
    public ArrayList<SpeedController> getControllers()
    {
        return m_motors;
    }
    
    /**
     * get a single motor controller
     * @param index
     * @return
     */
    public SpeedController getController(int index)
    {
        return m_motors.get(index);
    }
}
