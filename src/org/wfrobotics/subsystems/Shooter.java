package org.wfrobotics.subsystems;

import org.wfrobotics.commands.Rev;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter extends Subsystem 
{
    private final CANTalon flywheelTop;
    private final CANTalon flywheelBottom;
    private double m_speedDesired;

    public Shooter()
    {
        flywheelTop = new CANTalon(RobotMap.SHOOTER_MOTOR_SRX);
        
//      This is commented out until we get a sensor on the shooter
//        flywheelTop.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
//        
//        flywheelTop.changeControlMode(TalonControlMode.Speed);
//        flywheelTop.setPID(.115,0.0001,0.015);
//        flywheelTop.setCloseLoopRampRate(.01);    
       flywheelTop.setInverted(true);  //is this needed?
      
        flywheelBottom = new CANTalon(RobotMap.FEEDER_MOTOR_SRX);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Rev(Rev.MODE.OFF));
    }

    /**
     * Control speed of the shooting wheel(s)
     * @param rpm (usually between 3500 and 4000)
     * @return current speed the shooter wheel is running at
     */
    public double setSpeed(double rpm)
    {
        flywheelTop.set(rpm);        
        flywheelBottom.set(rpm);
        m_speedDesired = rpm;
        printDash();
        
        return flywheelTop.getSpeed();
    }
    
    /**
     * Tells if the current speed is at the previously set speed within this tolerance
     * @param tolerance percent above or below that counts as being at that speed (ex: .1 = +/-10%) 
     * @return if the shooting wheel(s) is at that speed
     */
    public boolean speedReached(double tolerance)
    {
        // TODO DRL should our return value be based on both flywheels?        
        return Math.abs(m_speedDesired - flywheelTop.getSpeed()) <= tolerance;
    }

    public void printDash()
    {
        SmartDashboard.putNumber("ShooterSpeedDesired", m_speedDesired);
        SmartDashboard.putNumber("FlywheelTopSpeedActual", flywheelTop.getSpeed());
        SmartDashboard.putNumber("FlywheelBottomSpeedActual", flywheelBottom.getSpeed());
    }
}
