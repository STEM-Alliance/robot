package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
 
public class LiftToPort extends InstantCommand
{
	private int level;
	private double height;
	double desired;
	double LiftHeight;

    public LiftToPort(int level)  
    {
    	requires(Lift.getInstance());
    	LiftHeight = Lift.getInstance().getHeight();
    	this.level = level;
    	this.height = ((level*28-0.5)-LiftHeight);
    	//May require positive/negative flip
    }
    
    protected void initialize()
    {
    	double Position = Lift.getInstance().getPosition();
    	SmartDashboard.putNumber("Current Position",Position);
    }
}