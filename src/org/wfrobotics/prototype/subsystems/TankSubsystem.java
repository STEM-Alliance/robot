package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.DriveTank;
import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

public class TankSubsystem extends Subsystem {

    public CANTalon RightFront;
    public CANTalon RightBack;
    public CANTalon LeftFront;
    public CANTalon LeftBack;
    
    public TankSubsystem()
    {
        this.RightFront = new CANTalon(10);
        this.RightBack = new CANTalon(11);
        this.LeftFront= new CANTalon(12);
        this.LeftBack = new CANTalon(13);
         
        // TODO Auto-generated constructor stub
    }
    
    public void setSpeedRight(double speed)
    {
       RightFront.set(speed); 
       RightBack.set(speed); 
    } 
    
    public void setSpeedLeft(double speed)
    {
        LeftFront.set(speed); 
        LeftBack.set(speed); 
    }
    
    public boolean getSpeedR()
    {
        return false;
    }

    public TankSubsystem(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new DriveTank());
        // TODO Auto-generated method stub

    }

}
