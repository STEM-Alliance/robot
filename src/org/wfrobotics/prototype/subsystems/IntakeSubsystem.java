package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.BoxIn;
import org.wfrobotics.prototype.commands.Stop;


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
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public IntakeSubsystem()
    {
      RightIntake = new TalonSRX(19);
      LeftIntake = new TalonSRX(20);
      RightIntake.setInverted(true);
      LeftIntake.set(ControlMode.Follower, 1);
      BoxCheck = new DigitalInput(0);
        // TODO Auto-generated constructor stub
    }
    
     public boolean hasCube(){
         return false;
     }
    public void initDefaultCommand() {
        setDefaultCommand(new Stop());
        
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    public void SetSpeed(double rpm)
    {
       RightIntake.set(ControlMode.PercentOutput, rpm);
      
    }
    public boolean CheckBox(){
        return BoxCheck.get();
      }
    public void HoldBox(){
        if (CheckBox() == true)
        {
            setDefaultCommand(new BoxIn());
        }
        else {
            setDefaultCommand(new Stop());
        }
    }
    
}

