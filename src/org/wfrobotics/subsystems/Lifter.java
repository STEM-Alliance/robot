package org.wfrobotics.subsystems;

import org.wfrobotics.commands.Lift;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lifter extends Subsystem
{
    private final boolean DEBUG = true;
//    private final double TOP_LIMIT = .0775;
//    private final double BOTTOM_LIMIT = 50;
    private double TOP = -684;
    private double BOTTOM = -885;
    private final double P = 1;
    private final double I = 0.07;
    private final double F = 0;
    
    private final CANTalon motor;
    private final DigitalInput senseGear;
    
    public Lifter(boolean reverseSensor)
    {        
        motor = new CANTalon(RobotMap.LIFTER_MOTOR);
        motor.setFeedbackDevice(FeedbackDevice.AnalogPot);
        motor.changeControlMode(TalonControlMode.Position);
//        motor.setForwardSoftLimit(TOP_LIMIT);
//        motor.setReverseSoftLimit(BOTTOM_LIMIT);
        motor.configNominalOutputVoltage(0, 0);
        motor.configPeakOutputVoltage(12, -12);
        motor.ConfigFwdLimitSwitchNormallyOpen(true);
        motor.ConfigRevLimitSwitchNormallyOpen(true);
        motor.setPID(P, I, 0, F, 0, 100, 0);
        motor.setAllowableClosedLoopErr(0);
        
        motor.enableForwardSoftLimit(false);
        motor.enableReverseSoftLimit(false);
        motor.enableLimitSwitch(true, true);
        motor.reverseOutput(true);
        motor.setInverted(false);
        motor.reverseSensor(true);
        
        motor.enableBrakeMode(false);
        motor.enableControl();
        motor.set(motor.getPosition());  // Recommended for closed loop control        
        
        senseGear = new DigitalInput(RobotMap.LIFTER_SENSOR_GEAR);
    }

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Lift());
    }
    
    public void disabled()
    {
        motor.disableControl();
        motor.setP(Preferences.getInstance().getDouble("LifterPID_P", P));
        motor.setI(Preferences.getInstance().getDouble("LifterPID_I", I));
        motor.setF(Preferences.getInstance().getDouble("LifterPID_F", F));
        TOP = Preferences.getInstance().getDouble("LifterTop", TOP);
        BOTTOM = Preferences.getInstance().getDouble("LifterBot", BOTTOM);   
//      motor.setForwardSoftLimit(Preferences.getInstance().getDouble("LifterTopL", TOP_LIMIT));
//      motor.setReverseSoftLimit(Preferences.getInstance().getDouble("LifterBottomL", BOTTOM_LIMIT));    
        motor.enableControl();
        motor.set(motor.getPosition());  // Recommended for closed loop control        

        SmartDashboard.putNumber("LifterAngle", motor.getPosition());
        }
    
    public boolean hasGear()
    {
        boolean hasGear = !senseGear.get();
        
        if (DEBUG)
        {
            SmartDashboard.putBoolean("LifterGear", hasGear);
        }
        
        return hasGear;
    }
    
    public void set(boolean goToTop)
    {
        double desired = (goToTop) ? TOP : BOTTOM;
        
        if (DEBUG)
        {
            SmartDashboard.putNumber("LifterDesired", desired);
            printDash();
        }
        
        if (Math.abs(desired - motor.getPosition()) < 10)
        {
            motor.ClearIaccum();  // Prevent I wind-up when close enough
        }
        
        motor.set(desired);
    }
    
    private void printDash()
    {
        String position;
        double angle = motor.getPosition();
        
        if (Math.abs(angle - TOP) < 10 || motor.isRevLimitSwitchClosed())
        {
            position = "Top";
        }
        else if (Math.abs(angle - BOTTOM) < 10 || motor.isFwdLimitSwitchClosed())
        {
            position = "Bottom";
        }
        else
        {
            position = "Moving";
        }
        
        SmartDashboard.putString("LifterState", position);
        SmartDashboard.putNumber("LifterAngle", angle);
    }

    public boolean atBottom()
    {
        return Math.abs(motor.getPosition() - BOTTOM) < .02 || motor.isFwdLimitSwitchClosed();
    }

    public boolean atTop()
    {
        return Math.abs(motor.getPosition() - TOP) < .02 || motor.isRevLimitSwitchClosed();
    }
}
