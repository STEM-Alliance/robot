package org.wfrobotics.subsystems;

import java.util.ArrayList;

import org.wfrobotics.commands.IntakeSetup;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Intake extends Subsystem {

    private ArrayList<CANTalon> m_motors;

    public Intake()
    {
        m_motors = new ArrayList<CANTalon>();
        
        for (int i = 0; i < RobotMap.INTAKE_MOTOR_SRX.length; i++)
        {
            CANTalon motor = new CANTalon(RobotMap.INTAKE_MOTOR_SRX[i]);
            motor.setInverted(RobotMap.INTAKE_MOTOR_INVERT[i]);
            m_motors.add(motor);
        }
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new IntakeSetup(false));
    }

    /**
     * Control speed of the ball intake roller
     * @param speed -1 (full outward) to 1 (full inward)
     */
    public void setSpeed(double speed)
    {
        for (int i = 0; i < m_motors.size(); i++)
        {
            m_motors.get(i).set(speed);
        }
    }
}
