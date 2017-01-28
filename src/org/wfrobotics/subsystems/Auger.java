package org.wfrobotics.subsystems;

import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Auger extends Subsystem {

    private CANTalon m_motor; 
    
    public Auger() 
    {
        m_motor = new CANTalon(RobotMap.AUGER_MOTOR);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        
    }
    /**
     * control speed of the auger wheels
     * @param rpm speed of the motor
     * @return current speed of the motor
     */
    public double setSpeed (double rpm)
    {
        m_motor.set(rpm);
        
        return m_motor.getSpeed();
    }
}