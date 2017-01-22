package org.wfrobotics.subsystems;

import org.wfrobotics.robot.RobotMap;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Climber extends Subsystem
{
    private CANTalon motor;
    
    public Climber()
    {
        motor = new CANTalon(RobotMap.CLIMBER_MOTOR_SRX);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }

    /**
     * Control speed of the climbing spool
     * @param speed -1 (full down) to 1 (full up)
     */
    public void setSpeed(double speed)
    {
        DriverStation.reportError("Climber set speed not implemented yet", true);
        motor.set(0);
    }
}
