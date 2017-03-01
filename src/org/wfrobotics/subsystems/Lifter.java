package org.wfrobotics.subsystems;

import org.wfrobotics.commands.Lift;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Lifter extends Subsystem
{
    private enum POSITION {TOP, BOTTOM, MOVING};
    
    private final double MOTOR_SPEED = 1;
    
    private final CANTalon motor;
    private final DigitalInput senseTop;
    private final DigitalInput senseBottom;
    private final DigitalInput senseGear;
    
    private POSITION stateLast;
    
    public Lifter()
    {        
        motor = new CANTalon(RobotMap.LIFTER_MOTOR);
        senseTop = new DigitalInput(RobotMap.LIFTER_SENSOR_TOP);
        senseBottom = new DigitalInput(RobotMap.LIFTER_SENSOR_BOTTOM);
        senseGear = new DigitalInput(RobotMap.LIFTER_SENSOR_GEAR);
        stateLast = POSITION.MOVING;
    }

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Lift());
    }
    
    public boolean hasGear()
    {
        return senseGear.get();
    }
    
    public void set(boolean goToTop)
    {
        double motorSpeed = 0;
        POSITION stateCurrent = get();
        
        if (stateCurrent != stateLast)  // Switched states, change the brake
        {
            boolean setBrake = (stateCurrent == POSITION.TOP || stateCurrent == POSITION.BOTTOM);
            
            motor.enableBrakeMode(setBrake);
        }
        
        if (stateCurrent == POSITION.MOVING)
        {
            motorSpeed = (goToTop) ? MOTOR_SPEED : -MOTOR_SPEED;
        }
        
        stateLast = stateCurrent;
        motor.set(motorSpeed);
    }
    
    private POSITION get()
    {
        if (senseTop.get())
        {
            return POSITION.TOP;
        }
        else if (senseBottom.get())
        {
            return POSITION.BOTTOM;
        }
        else
        {
            return POSITION.MOVING;
        }
    }
}
