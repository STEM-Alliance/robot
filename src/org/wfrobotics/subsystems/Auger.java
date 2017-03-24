package org.wfrobotics.subsystems;

import org.wfrobotics.commands.Conveyor;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Screw conveyor or auger conveyor
 * This subsystem controls the flighting/auger that acts as a ball conveyor
 *
 */
public class Auger extends Subsystem {

    private CANTalon m_motor; 
    private CANTalon m_motorFeeder;
    public Auger() 
    {
        m_motor = new CANTalon(RobotMap.AUGER_MOTOR);
        m_motor.setInverted(true);  //is this needed?

        m_motorFeeder = new CANTalon(RobotMap.NEW_FEEDER_MOTOR_SRX);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Conveyor(Conveyor.MODE.OFF));
    }
    
    /**
     * control speed of the auger wheels
     * @param rpm speed of the motor
     */
    public void setSpeed (double rpm)
    {
        m_motor.set(rpm);
        m_motorFeeder.set(rpm);
    }
}