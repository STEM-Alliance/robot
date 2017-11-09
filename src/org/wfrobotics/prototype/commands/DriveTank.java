package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;
import org.wfrobotics.prototype.subsystems.TankSubsystem;
import org.wfrobotics.reuse.controller.Xbox;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTank extends Command {
    
    public double speedL;
    public double speedR;

    public DriveTank()
    {
        // TODO Auto-generated constructor stub
    }
    
    public DriveTank(double speed)
    {
        Robot.tankSubsystem.setSpeedLeft(speed);
        Robot.tankSubsystem.setSpeedRight(speed);
    }
    
    public void execute()
    {
     this.speedL = Robot.controls.getLeftJoystick();
     Robot.tankSubsystem.setSpeedLeft(speedL);
     
     this.speedR = Robot.controls.getRightJoystick();
     Robot.tankSubsystem.setSpeedRight(speedR);
     
    }

    @Override
    protected boolean isFinished()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
