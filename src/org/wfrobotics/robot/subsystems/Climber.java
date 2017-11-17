package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.CANTalonFactory;
import org.wfrobotics.robot.commands.Up;
import org.wfrobotics.robot.config.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

public class Climber extends Subsystem
{
    private CANTalon motor;
    //private DigitalInput sensor;

    public Climber()
    {
        motor = CANTalonFactory.makeTalon(RobotMap.CLIMBER_MOTOR_SRX);
        //sensor = new DigitalInput(RobotMap.CLIMBER_SENSOR_DIGITAL);
    }

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Up(Up.MODE.VARIABLE_SPEED));
    }

    /**
     * Control speed of the climbing spool
     * @param speed -1 (full down) to 1 (full up)
     */
    public void setSpeed(double speed)
    {
        if(speed >= 0)
        {
            motor.set(speed);
        }
    }

    public boolean isAtTop()
    {
        return false;//sensor.get(); //TODO: make sure sensor = HIGH is on; This could be backwards
    }
}
