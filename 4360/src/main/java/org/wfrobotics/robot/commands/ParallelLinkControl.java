package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.IntakeMotor;
import org.wfrobotics.robot.subsystems.ParallelLink;
import edu.wpi.first.wpilibj.command.Command;

public class ParallelLinkControl extends Command 
{
    double speed;
    
    public ParallelLinkControl(double speed) 
    {
    	this.speed = speed;
    	requires(ParallelLink.getInstance());
    }

    protected void initialize() 
    {
    }

    protected void execute() 
    {
        ParallelLink.getInstance().setlink(speed);
    }

    protected boolean isFinished() 
    {
        return false;
    }
}