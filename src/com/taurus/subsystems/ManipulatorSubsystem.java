package com.taurus.subsystems;

import com.taurus.commands.ManipulatorStop;
import com.taurus.robot.RobotMap;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ManipulatorSubsystem extends Subsystem {
    
    private CANTalon motorArm;

    public ManipulatorSubsystem()
    {
        motorArm = new CANTalon(RobotMap.CAN_MANIPULATOR_TALON);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ManipulatorStop());
    }
    
    public void setSpeed(double speed){
        motorArm.set(speed);
    }
    
}
