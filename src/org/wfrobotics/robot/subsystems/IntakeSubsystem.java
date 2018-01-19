package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.config.RobotMap;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class IntakeSubsystem extends Subsystem {

    TalonSRX rightMtr,
             leftMtr;
    
    public IntakeSubsystem()
    {
        rightMtr = new TalonSRX(RobotMap.INTAKE_MOTOR[0]);
        leftMtr = new TalonSRX(RobotMap.INTAKE_MOTOR[1]);
        leftMtr.setInverted(true);
        leftMtr.set(ControlMode.Follower, RobotMap.INTAKE_MOTOR[0]);
    }
    
    @Override
    protected void initDefaultCommand()
    {
        
    }
    /**
     *  Sets the speed of the intakes motor
     */
    public void setSpeed(double speed)
    {
        rightMtr.set(ControlMode.Velocity, speed);
    }
    /**
     * Evaluates if the robot has the cube in it!
     * TODO: figure out how this works with a limit switch or something
     * @return true 
     */
    public boolean hasCube()
    {
        return false;
    }
    

}
