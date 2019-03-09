package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.IntakeMotor;
import org.wfrobotics.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LiftManual extends Command 
{
	double speed;
	
	public LiftManual(double speed)
	{	
		this.speed = speed;
		requires(Lift.getInstance());	
	}

	protected void execute()
    {
        Lift.getInstance().setSpeed(speed);
	}
	
	protected void initialize()
    {	
    //	desired = degrees + Position;
    //	Robot.prototypeSubsystem.setHeight(degrees);	
    //  TODO do anything this Command needs to happen ONCE
    }
	
	protected boolean isFinished()
    {
        return true;
    }
}