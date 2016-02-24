package com.taurus.subsystems;

import com.taurus.robot.RobotMap;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ShooterSubsystem extends Subsystem 
{    
    private final double BALL_RELEASE_ANGLE_EXTENDED = 120;
    private final double BALL_RELEASE_ANGLE_CONTRACTED = 0;
    private final double TOLERANCE = 1;
    
    public DigitalInput stopSwitch;
    
    private CANTalon shooterFT;
    private CANTalon shooterFB;
   
    
    private Relay leds;

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
       
        leds = new Relay(RobotMap.PIN_RELAY_LEDS, Direction.kForward);
    }
    
    public void initDefaultCommand() 
    {
        // Set the default command for a subsystem here.
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
  
   
    
    public void enableLEDs(boolean enable)
    {
        if(enable)
        {
            leds.set(Value.kForward);
        }
        else
        {
            leds.set(Value.kOff);
        }
    }
}
