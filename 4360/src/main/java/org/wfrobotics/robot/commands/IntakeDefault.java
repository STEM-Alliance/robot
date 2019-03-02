package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.Command;

public class IntakeDefault extends Command
{
	public IntakeDefault()
	{
		requires(Intake.getInstance());
	}
	
	@Override
	protected boolean isFinished() 
	{
		return false;
	}
}
