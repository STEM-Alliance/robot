package org.wfrobotics.subsystems;

import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;

public class ShooterSubsystem extends Subsystem {

    private CANTalon m_motor;
    private double m_currentSpeed;

    public ShooterSubsystem()
    {
        m_motor = new CANTalon(RobotMap.SHOOTER_MOTOR_SRX);
        m_motor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        m_motor.changeControlMode(TalonControlMode.Speed);
        m_motor.setPID(.115,0.0001,0.015);
        m_motor.setCloseLoopRampRate(.01);
        
        //is this needed?
        m_motor.setInverted(true);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }

    /**
     * Control speed of the shooting wheel(s)
     * @param speed speed in rpm (usually between 3500 and 4000)
     * @return current speed the shooter wheel is running at
     */
    public double setSpeed(double speed)
    {
        m_currentSpeed = speed;

        m_motor.set(m_currentSpeed);
        
        return m_motor.getSpeed();
    }
    
    /**
     * Tells if the current speed is at the previously set speed within this tolerance
     * @param tolerance percent above or below that counts as being at that speed (.1 = +/-10%) 
     * @return if the shooting wheel(s) is at that speed
     */
    public boolean speedReached(double tolerance)
    {
        // get actual value based on sensor,
        // compare to setpoint within specified tolerance
        if(Math.abs(m_currentSpeed - m_motor.getSpeed()) <= tolerance)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
