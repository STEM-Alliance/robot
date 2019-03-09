package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.IntakeMotor;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Command;

public class StopIntake extends Command 
{
    public StopIntake() 
    {
       requires(IntakeMotor.getInstance());
    }

    protected void initialize() 
    {
    	IntakeMotor.getInstance().setspeed(0);	
    }

    protected void execute() 
    {
    }

    protected boolean isFinished() 
    {
        return false;
    }

    protected void end() 
    {
    }

    protected void interrupted() 
    {
    }
}