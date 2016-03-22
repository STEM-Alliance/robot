package com.taurus.subsystems;

import com.taurus.commands.KickerStop;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeSubsystem extends Subsystem
{
    private CANTalon motor;

    public IntakeSubsystem()
    {
        motor = new CANTalon(RobotMap.CAN_KICKER_TALON);
        motor.enableBrakeMode(false);
        motor.setInverted(false);  
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new KickerStop());
    }
    
    /***
     * basic speed function 
     * @param speed
     */
    public void setSpeed(double speed)
    {   
        motor.set(speed);
        SmartDashboard.putNumber("KickerSpeed", speed);
    }
    
}
