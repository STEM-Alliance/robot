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
        
        flywheelTop.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        flywheelTop.changeControlMode(TalonControlMode.Speed);
        flywheelTop.setPID(.115,0.0001,0.015);
        flywheelTop.setCloseLoopRampRate(.01);
        
        flywheelTop.setInverted(false);
      
        flywheelBottom = new CANTalon(RobotMap.FEEDER_MOTOR_SRX);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Rev(Rev.MODE.OFF));
    }

    /**
     * Control speed of the TOP shooting wheel
     * @param rpm (usually between 3500 and 4000)
     * @return current speed the shooter wheel is running at
     */
    public double setSpeedTop(double rpm)
    {
        flywheelTop.set(rpm);        
        m_speedDesired = rpm;
        printDash();
        
        return flywheelTop.getSpeed();
    }
    /**
     * Control speed of the BOTTOM shooting wheel
     * @param rpm (usually between 3500 and 4000)
     * @return current speed the shooter wheel is running at
     */
    public double setSpeedBottom(double rpm)
    {
        flywheelBottom.set(rpm);
        m_speedDesired = rpm;
        
        return flywheelBottom.getSpeed();
    }
    
    /**
     * Tells if the current speed is at the previously set speed within this tolerance
     * @param tolerance percent above or below that counts as being at that speed (ex: .1 = +/-10%) 
     * @return if the shooting wheel(s) is at that speed
     */
    public boolean topSpeedReached(double tolerance)
    {    
        boolean reached = Math.abs(m_speedDesired - flywheelTop.getSpeed()) <= tolerance;
        SmartDashboard.putBoolean("TopSpeedReached", reached);
        return reached;
    }
    
    public boolean bottomSpeedReached(double tolerance)
    {
        boolean reached = Math.abs(m_speedDesired - flywheelBottom.getSpeed()) <= tolerance;
        SmartDashboard.putBoolean("BottomSpeedReached", reached);
        return reached;
    }
    
    /**
     * Test to see if the top motor has REVed to a certain speed then starts to spin the 
     * bottom flywheel to get it up to speed
     * 
     * @param rpm to set flywheels to
     * @param tolerance tolerance in rpm used to turn on wheels in sequence
     * @return if the desired speed is reached
     */
    public boolean topThenBottom(double rpm, double tolerance)
    {
        boolean atSpeed = false;
        
        if (rpm != 0)
        {
            flywheelTop.set(rpm);
            
            if(topSpeedReached(tolerance))
            {
                flywheelBottom.set(rpm);
                
                if(bottomSpeedReached(tolerance))
                {
                    atSpeed = true;
                }
            }
            else
            {
                //SmartDashboard.putBoolean("Desired speed", topSpeedReached(tolerance));
            }
        }
        else
        {
            flywheelBottom.set(0);
            
            //testing if the bottom flywheel is below a threshold before turning off the top one
            if (bottomSpeedReached(100))
            {
                flywheelTop.set(0);
            }
        }
        
        return atSpeed;
    }
    
    /**
     * Check if both flywheels are within a tolerance
     * @param tolerance
     * @return
     */
    public boolean bothInTolerance(double tolerance)
    {
        if (topSpeedReached(tolerance))
        {
            if (bottomSpeedReached(tolerance))
            {
                return true;
            }
        }       
        return false;
    }
    
    public double getSpeedTop()
    {
        return flywheelTop.getSpeed();
    }

    public void printDash()
    {
        SmartDashboard.putNumber("ShooterSpeedDesired", m_speedDesired);
        SmartDashboard.putNumber("FlywheelTopSpeedActual", flywheelTop.getSpeed());
        SmartDashboard.putNumber("FlywheelBottomSpeedActual", flywheelBottom.getSpeed());
    }
}
