package org.wfrobotics.subsystems;

import org.wfrobotics.commands.Up;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Climber extends Subsystem
{
    private CANTalon motor;
    private DigitalInput sensor;
    
    public Climber()
    {
        motor = new CANTalon(RobotMap.CLIMBER_MOTOR_SRX);
        sensor = new DigitalInput(RobotMap.CLIMBER_SENSOR_DIGITAL);
    }
    
    @Override
    protected void initDefaultCommand()
    
    {
         setDefaultCommand(new Up(Up.MODE.OFF));
    }

    /**
     * Control speed of the climbing spool
     * @param speed -1 (full down) to 1 (full up)
     */
    public void setSpeed(double speed)
    {
        motor.set(speed);
    }
    
    public boolean isAtTop()
    {
        return false;//sensor.get(); //TODO: make sure sensor = HIGH is on; This could be backwards     
    }
}
