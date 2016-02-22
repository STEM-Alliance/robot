package com.taurus.subsystems;

import com.taurus.robot.RobotMap;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ManipulatorSubsystem extends Subsystem {
    
    private CANTalon motorArm;
    private double speed;

    public ManipulatorSubsystem()
    {
        motorArm = new CANTalon(RobotMap.PIN_MANIPULATOR);
        
    }

    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
        
    }
    
    public void rotate( double speed){
        motorArm.set(speed);
    }
    
}
