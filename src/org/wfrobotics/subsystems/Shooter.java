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

    private final double TOP_P = 0.2;
    private final double TOP_I = 0.00015;
    private final double TOP_D = 0.001;
    
    // 100% of total feed forward / native counts per 100ms @ 4000rpm
    private final double TOP_F = 0;//1.0 * 1023 / ( 4000 * (1/60) * (1/10) * 4096);
    private final double TOP_RAMP = 0.01;

    private final double BOTTOM_P = 0.2;
    private final double BOTTOM_I = 0.0002;
    private final double BOTTOM_D = 0.001;
    
    // 100% of total feed forward / native counts per 100ms @ 4000rpm
    private final double BOTTOM_F = 0;//1.0 * 1023 / ( 4000 * (1/60) * (1/10) * 4096);
    private final double BOTTOM_RAMP = 0.01;
    
    public Shooter()
    {
        flywheelTop = new CANTalon(RobotMap.SHOOTER_MOTOR_SRX);
        
        flywheelTop.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        flywheelTop.changeControlMode(TalonControlMode.Speed);
        flywheelTop.setPID(TOP_P,TOP_I,TOP_D,TOP_F,0,TOP_RAMP,0);
        
        flywheelTop.reverseSensor(true);
        flywheelTop.setInverted(true);
      
        flywheelBottom = new CANTalon(RobotMap.FEEDER_MOTOR_SRX);
        
        flywheelBottom.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        flywheelBottom.changeControlMode(TalonControlMode.Speed);
        flywheelBottom.setPID(BOTTOM_P,BOTTOM_I,BOTTOM_D,BOTTOM_F,0,BOTTOM_RAMP,0);
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
        
        return getSpeedTop();
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
        boolean reached = Math.abs(m_speedDesired - getSpeedTop()) <= tolerance;
        SmartDashboard.putNumber("SpeedDesired",m_speedDesired);
        SmartDashboard.putNumber("TopSpeedDiff",m_speedDesired - getSpeedTop());
        SmartDashboard.putBoolean("TopSpeedReached", reached);
        return reached;
    }
    
    public boolean bottomSpeedReached(double tolerance)
    {
        boolean reached = Math.abs(m_speedDesired - flywheelBottom.getSpeed()) <= tolerance*2;
        SmartDashboard.putNumber("TopSpeedDiff",m_speedDesired - flywheelBottom.getSpeed());
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
        printDash();

        m_speedDesired = rpm;
        
        if (rpm != 0)
        {
            flywheelTop.set(rpm);
            flywheelTop.enable();
            
            if(topSpeedReached(tolerance))
            {
                flywheelBottom.set(rpm);
                flywheelBottom.enable();
                
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
            flywheelBottom.disable();
            
            //testing if the bottom flywheel is below a threshold before turning off the top one
            if (bottomSpeedReached(100))
            {
                flywheelTop.set(0);
                flywheelTop.disable();
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
        return -flywheelTop.getSpeed();
    }

    public void printDash()
    {
        SmartDashboard.putNumber("ShooterSpeedDesired", m_speedDesired);
        SmartDashboard.putNumber("FlywheelTopSpeedActual", getSpeedTop());
        SmartDashboard.putNumber("FlywheelBottomSpeedActual", flywheelBottom.getSpeed());
    }
}
