package org.wfrobotics.subsystems;

import org.wfrobotics.Utilities;
import org.wfrobotics.commands.Up;
import org.wfrobotics.hardware.SharpDistance;
import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Climber extends Subsystem
{
    private CANTalon motor;
    private SharpDistance sensor;
    
    public Climber()
    {
        motor = new CANTalon(RobotMap.CLIMBER_MOTOR_SRX);
        sensor = new SharpDistance(RobotMap.CLIMBER_SENSOR_ANALOG);
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
        printDash();
        motor.set(speed);
    }
    
    public void printDash()
    {
        SmartDashboard.putNumber("ClimberDistance", Utilities.round(sensor.getDistance(),2));
    }

    public boolean isAtTop()
    {
        return (sensor.getDistance() < 3); 
        //TODO: make sure sensor = HIGH is on; This could be backwards     
    }
}
