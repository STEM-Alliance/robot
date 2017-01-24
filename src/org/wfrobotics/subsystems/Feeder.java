package org.wfrobotics.subsystems;

import org.wfrobotics.commands.Feed;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Feeder extends Subsystem {
    
    private CANTalon m_motor; 
    private double m_speedDesired; 
    
    public Feeder() 
    {
        m_motor = new CANTalon(RobotMap.FEEDER_MOTOR_SRX);
    }
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Feed(Feed.MODE.OFF));
    }
    /**
     * control speed of the feeder wheels
     * @param rpm speed of the motor
     * @return current speed of the motor
     */
    public double setSpeed (double rpm)
    {
        m_speedDesired = rpm;
        m_motor.set(m_speedDesired);
        
        printDash();
        
        return m_motor.getSpeed();
    }
    /**
     * Tells whether the feeder speed is at the set speed within the tolerance
     * @param tolerance percent above or below that counts as being at that speed (ex: .1 = +/-10%) 
     * @return if the feeder wheel(s) is at that speed
     */
    public boolean speedReached (double tolerance)
    {
        if (Math.abs(m_speedDesired - m_motor.getSpeed()) <= tolerance)
        {
            return true;
        }
        else 
        {
            return false;
        }
              
    }
    
    public void printDash()
    {
        SmartDashboard.putNumber("FeederSpeedDesired", m_speedDesired);
        SmartDashboard.putNumber("FeederSpeedActual", m_motor.getSpeed());
    }
}
