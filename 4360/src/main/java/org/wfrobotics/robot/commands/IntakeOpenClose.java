package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeOpenClose extends InstantCommand
{
	
	final boolean open;
    public IntakeOpenClose(boolean open)
    {
    	
    	
    	this.open = open;
    	requires(Intake.getInstance());
    }
    
    protected void initialize()
    {
    	if(open == true) 
    	{
    		Intake.getInstance().Forward();  
    		SmartDashboard.putBoolean("Current Value", open);
    	}
    	else
    	{
    		Intake.getInstance().Reverse();
    		SmartDashboard.putBoolean("Current Value", open);
    	}
    }
}