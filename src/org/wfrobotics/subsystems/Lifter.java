package org.wfrobotics.subsystems;

import org.wfrobotics.commands.Lift;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lifter extends Subsystem
{
    private final boolean DEBUG = true;
    private final double SOFTWARE_LIMIT_MARGIN = .05;
    private final double TOP_LIMIT = .6;
    private final double BOTTOM_LIMIT = .4;
    private final double TOP = TOP_LIMIT - SOFTWARE_LIMIT_MARGIN;
    private final double BOTTOM = BOTTOM_LIMIT + SOFTWARE_LIMIT_MARGIN;
    private final double P = .3;
    private final double I = .001;
    
    private final CANTalon motor;
    private final DigitalInput senseGear;
    
    public Lifter()
    {        
        motor = new CANTalon(RobotMap.LIFTER_MOTOR);
        motor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
        motor.changeControlMode(TalonControlMode.Position);
        motor.setForwardSoftLimit(TOP);
        motor.setReverseSoftLimit(BOTTOM);
        motor.configNominalOutputVoltage(0, 0);
        motor.configPeakOutputVoltage(11, -11);
        motor.ConfigFwdLimitSwitchNormallyOpen(true);
        motor.ConfigRevLimitSwitchNormallyOpen(true);
        motor.setPID(P, I, 0, 0, 0, 10, 0);
        motor.setAllowableClosedLoopErr(0);
        
        motor.enableForwardSoftLimit(true);
        motor.enableReverseSoftLimit(true);
        motor.enableLimitSwitch(true, true);
        motor.enableBrakeMode(true);
        motor.enableControl();
        motor.set(motor.getPosition());  // Recommended for closed loop control
        
        senseGear = new DigitalInput(RobotMap.LIFTER_SENSOR_GEAR);
    }

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Lift());
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
        
        motor.set(desired);
    }
    
    private void printDash()
    {
        String position;
        double angle = motor.getPosition();
        
        if (Math.abs(angle - TOP) < .02 || motor.isFwdLimitSwitchClosed())
        {
            position = "Top";
        }
        else if (Math.abs(angle - BOTTOM) < .02 || motor.isRevLimitSwitchClosed())
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
}
