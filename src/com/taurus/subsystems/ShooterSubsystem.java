package com.taurus.subsystems;

import com.taurus.commands.ShooterStop;
import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ShooterSubsystem extends Subsystem 
{    
    public DigitalInput stopSwitch;
    
    private CANTalon shooterFT;
    private CANTalon shooterFB;   
    
    /**
     * Constructor
     */
    public ShooterSubsystem() 
    {
        shooterFT = new CANTalon(RobotMap.CAN_SHOOTER_TALON_TOP);
        shooterFB = new CANTalon(RobotMap.CAN_SHOOTER_TALON_BOTTOM);
        
        shooterFT.setExpiration(1);
        shooterFB.setExpiration(1);
        
        shooterFT.setInverted(true);
        
        stopSwitch = new DigitalInput(RobotMap.PIN_DIO_SHOOTER_BALL_SENSOR);
    }
    
    public void initDefaultCommand() 
    {
        // Set the default command for a subsystem here.
        setDefaultCommand(new ShooterStop());        
    }    

    /**
     * Set outer wheel motor speed
     * @param topSpeed between -1 and 1
     * @param bottomSpeed between -1 and 1
     */
    public void setSpeed(double topSpeed, double bottomSpeed) 
    {
        shooterFT.set(topSpeed);
        shooterFB.set(bottomSpeed);
    }
}
