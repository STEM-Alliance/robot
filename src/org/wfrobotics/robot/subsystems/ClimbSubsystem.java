package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.config.RobotMap;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class ClimbSubsystem extends Subsystem {
    
    TalonSRX climbMtr;
    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub

    }
    public ClimbSubsystem()
    {
        climbMtr = new TalonSRX(RobotMap.CLIMB_MOTOR);
    }
    public boolean isAtBottem()
    {
        return false;
    }
    public void setSpeed(double speed)
    {
        
    }
    

}
