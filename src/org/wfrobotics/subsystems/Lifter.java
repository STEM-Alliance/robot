package org.wfrobotics.subsystems;

import org.wfrobotics.commands.Lift;
import org.wfrobotics.robot.RobotMap;
import org.wfrobotics.subsystems.drive.swerve.SwerveConstants;

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
    private final double TOP_LIMIT = .0775;
    private final double BOTTOM_LIMIT = 50;
    private double TOP = -.095;
    private double BOTTOM = .095;
    private final double P = .25;
    private final double I = 0.001;
    
    private final CANTalon motor;
    private final DigitalInput senseGear;
    
    public Lifter(boolean reverseSensor)
    {        
        motor = new CANTalon(RobotMap.LIFTER_MOTOR);
        motor.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
        motor.changeControlMode(TalonControlMode.Position);
        motor.setForwardSoftLimit(TOP_LIMIT);
        motor.setReverseSoftLimit(BOTTOM_LIMIT);
        motor.configNominalOutputVoltage(0, 0);
        motor.configPeakOutputVoltage(11, -11);
        motor.ConfigFwdLimitSwitchNormallyOpen(true);
        motor.ConfigRevLimitSwitchNormallyOpen(true);
        motor.setPID(P, I, 0, 0, 0, 10, 0);
        motor.setAllowableClosedLoopErr(0);
        
        motor.enableForwardSoftLimit(false);
        motor.enableReverseSoftLimit(false);
        motor.enableLimitSwitch(true, true);
        motor.reverseOutput(true);
        motor.setInverted(false);
        motor.reverseSensor(true);
        
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
    
    public void disabled()
    {
        motor.setP(Preferences.getInstance().getDouble("LifterPID_P", SwerveConstants.CHASSIS_PID_P));
        motor.setI(Preferences.getInstance().getDouble("LifterPID_I", SwerveConstants.CHASSIS_PID_I));
        TOP = Preferences.getInstance().getDouble("LifterTop", TOP);
        BOTTOM = Preferences.getInstance().getDouble("LifterBot", BOTTOM);   
//      motor.setForwardSoftLimit(Preferences.getInstance().getDouble("LifterTopL", TOP_LIMIT));
//      motor.setReverseSoftLimit(Preferences.getInstance().getDouble("LifterBottomL", BOTTOM_LIMIT));    
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
