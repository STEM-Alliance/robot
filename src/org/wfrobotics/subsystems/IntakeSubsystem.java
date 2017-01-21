package org.wfrobotics.subsystems;

import java.util.ArrayList;

import org.wfrobotics.commands.IntakeSetup;
import org.wfrobotics.commands.drive.DriveTankArcade;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

public class IntakeSubsystem extends Subsystem {

    private ArrayList<CANTalon> m_motors;

    public IntakeSubsystem()
    {
        m_motors = new ArrayList<CANTalon>();
        
        for (int i = 0; i < RobotMap.INTAKE_MOTOR_SRX.length; i++)
        {
            m_motors.add(new CANTalon(RobotMap.INTAKE_MOTOR_SRX[i]));
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
