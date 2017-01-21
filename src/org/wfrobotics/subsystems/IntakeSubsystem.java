package org.wfrobotics.subsystems;

import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

public class IntakeSubsystem extends Subsystem {

    private CANTalon m_motor;

    public IntakeSubsystem()
    {
        m_motor = new CANTalon(RobotMap.INTAKE_MOTOR_SRX);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }

    /**
     * Control speed of the ball intake roller
     * @param speed -1 (full outward) to 1 (full inward)
     */
    public void setSpeed(double speed)
    {
        m_motor.set(speed);
    }
}
