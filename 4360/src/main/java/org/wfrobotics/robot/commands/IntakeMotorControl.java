package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.IntakeMotor;
import org.wfrobotics.robot.subsystems.Lift;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class IntakeMotorControl extends Command 
{
	double speed;
	
    public IntakeMotorControl(double speed)
    {
		this.speed = speed;
		requires(IntakeMotor.getInstance());
    }
    
    protected void execute()
    {
        IntakeMotor.getInstance().setspeed(speed);
    }

	protected void initialize()
    {	
    //	desired = degrees + Position;
    //	Robot.prototypeSubsystem.setHeight(degrees);    	
    }

	protected boolean isFinished()
    {
        return true;
    }
}