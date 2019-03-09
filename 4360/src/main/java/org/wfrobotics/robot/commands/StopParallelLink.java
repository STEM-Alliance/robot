package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.IntakeMotor;
import org.wfrobotics.robot.subsystems.ParallelLink;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Command;

public class StopParallelLink extends Command 
{
    public StopParallelLink() 
    {
       requires(ParallelLink.getInstance());
    }

    protected void initialize() 
    {
    	ParallelLink.getInstance().setlink(0);
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