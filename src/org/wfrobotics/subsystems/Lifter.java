package org.wfrobotics.subsystems;

import org.wfrobotics.commands.Lift;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Lifter extends Subsystem
{
    private enum POSITION {TOP, BOTTOM, MOVING};
    
    private final double MOTOR_SPEED = .5;
    
    private final CANTalon motor;
    private final DigitalInput senseGear;
    
    public Lifter()
    {        
        motor = new CANTalon(RobotMap.LIFTER_MOTOR);
        motor.ConfigFwdLimitSwitchNormallyOpen(true);
        motor.ConfigRevLimitSwitchNormallyOpen(true);
        motor.enableLimitSwitch(false, false);
        motor.enableBrakeMode(true);
        // TODO DRL Set soft limits before the hardware limit sensors, then remove software to limit motor speed, just set it
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
        
        SmartDashboard.putBoolean("LifterGear", hasGear);
        return hasGear;
    }
    
    public void set(boolean goToTop)
    {
        double motorSpeed = 0;  // Set to zero when not moving so brake engages
        POSITION stateCurrent = get();
        
        if (goToTop && get() != POSITION.TOP)
        {
            motorSpeed = MOTOR_SPEED;
        }
        else if(!goToTop && get() != POSITION.BOTTOM)
        {
            motorSpeed = -MOTOR_SPEED;
        }
//        if (stateCurrent == POSITION.MOVING)
//        {
//            motorSpeed = (goToTop) ? MOTOR_SPEED : -MOTOR_SPEED;
//        }
        SmartDashboard.putNumber("LifterSpeed", motorSpeed);
        motor.set(motorSpeed);
    }
    
    private POSITION get()
    {
        POSITION p;
        
        if (motor.isFwdLimitSwitchClosed())
        {
            p = POSITION.BOTTOM;
        }
        else if (motor.isRevLimitSwitchClosed())
        {
            p = POSITION.TOP;
        }
        else
        {
            p = POSITION.MOVING;
        }
        SmartDashboard.putString("LifterState", p.toString());
        return p;
    }
}
