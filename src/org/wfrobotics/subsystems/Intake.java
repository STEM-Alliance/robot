package org.wfrobotics.subsystems;

import org.wfrobotics.commands.IntakeSetup;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Intake extends Subsystem 
{
    CANTalon motor;

    public Intake()
    {
        motor = new CANTalon(RobotMap.INTAKE_MOTOR_SRX);
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
        motor.set(speed);
    }
}
