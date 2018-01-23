package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.BoxHold;

import org.wfrobotics.prototype.commands.IntakeStop;


import com.ctre.phoenix.motorcontrol.ControlMode;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class IntakeSubsystem extends Subsystem {
    TalonSRX RightIntake;
    TalonSRX LeftIntake;
    DigitalInput BoxCheck;
 
    public IntakeSubsystem()
    {
      RightIntake = new TalonSRX(10);
      LeftIntake = new TalonSRX(20);
      RightIntake.setInverted(true);
      LeftIntake.set(ControlMode.Follower, 1);
      BoxCheck = new DigitalInput(0);

    }
    
     public boolean hasCube(){
         return false;
     }
    public void initDefaultCommand() {
        setDefaultCommand(new IntakeStop());
        
       
    }
    public void setSpeed(double rpm)
    {
       RightIntake.set(ControlMode.PercentOutput, rpm);
      
    }
    public boolean CheckBox(){
        return BoxCheck.get();
      }
    public void holdBox(){
        
        if (CheckBox() == false)
        {
           setDefaultCommand(new BoxHold());
            
        }
        if (CheckBox() == true){
            setDefaultCommand(new IntakeStop());
        }
    }
    
}

