package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.Wait;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class IntakeSubsystem extends Subsystem {
    CANTalon RightIntake;
    CANTalon LeftIntake;
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public IntakeSubsystem()
    {
      RightIntake = new CANTalon(19);
      LeftIntake = new CANTalon(20);
      RightIntake.setInverted(true);
      LeftIntake.set(ControlMode.Follower, 1);
        // TODO Auto-generated constructor stub
    }
    
     public boolean hasCube(){
         return false;
     }
    public void initDefaultCommand() {
        setDefaultCommand(new Wait());
        
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    public void SetSpeed(double rpm)
    {
       RightIntake.set(ControlMode.PercentOutput, rpm);
      
    }
  
    
}

